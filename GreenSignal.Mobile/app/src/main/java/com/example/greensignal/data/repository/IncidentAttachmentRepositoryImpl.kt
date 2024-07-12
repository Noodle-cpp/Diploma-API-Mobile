package com.example.greensignal.data.repository

import android.app.Application
import android.content.SharedPreferences
import com.example.greensignal.data.remote.GreenSignalApi
import com.example.greensignal.domain.repository.IncidentAttachmentRepository
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class IncidentAttachmentRepositoryImpl @Inject constructor(
    private val greenSignalApi: GreenSignalApi,
    private val appContext: Application,
    private val prefs: SharedPreferences
): IncidentAttachmentRepository {
    override suspend fun attachFileToIncident(
        multipartImage: MultipartBody.Part,
        description: RequestBody,
        id: String,
        token: String
    ): Resource<String> {
        try {
            greenSignalApi.createIncidentAttachment(id, token, multipartImage, description)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> {
                    return Resource.Error("Проверьте корректность введённых данных.")
                }
                401 -> {
                    return Resource.Error("Введены некорректные данные.")
                }
                403 -> {
                    prefs.edit()
                        .putString("inspector-jwt-token", null)
                        .apply()
                    return Resource.Error("Отказано в доступе.")
                }
                404 -> {
                    return Resource.Error("Информация не найдена.")
                }
                422 -> {
                    return Resource.Error("Проверьте корректность введённых данных.")
                }
                429 -> {
                    return Resource.Error("Превышен лимит запросов. Попробуйте позже.")
                }
            }
            return Resource.Error(e.message ?: "Что-то пошло не так")
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так\n" + e.message)
        }

        return Resource.Success()
    }
}