package com.example.greensignal.data.repository

import android.app.Application
import android.content.SharedPreferences
import com.example.greensignal.data.remote.GreenSignalApi
import com.example.greensignal.domain.repository.StorageRepository
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class StorageRepositoryImpl @Inject constructor(
    private val greenSignalApi: GreenSignalApi,
    private val appContext: Application,
): StorageRepository {
    override suspend fun downloadFileFromStorage(path: String): Resource<ResponseBody> {
        val response = try {
            greenSignalApi.downloadFileFromStorage(path)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> {
                    return Resource.Error("Проверьте корректность введённых данных.")
                }
                401 -> {
                    return Resource.Error("Введены некорректные данные.")
                }
                403 -> {
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
            return Resource.Error("Что-то пошло не так")
        }

        return Resource.Success(response)
    }
}