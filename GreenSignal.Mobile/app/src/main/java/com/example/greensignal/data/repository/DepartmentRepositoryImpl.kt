package com.example.greensignal.data.repository

import android.content.SharedPreferences
import com.example.greensignal.data.remote.GreenSignalApi
import com.example.greensignal.data.remote.dto.response.DepartmentDto
import com.example.greensignal.domain.repository.DepartmentRepository
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class DepartmentRepositoryImpl @Inject constructor(
    private val greenSignalApi: GreenSignalApi,
    private val prefs: SharedPreferences
): DepartmentRepository {
    override suspend fun getDepartments(
        page: Int?,
        perPage: Int?,
        token: String
    ): Resource<MutableList<DepartmentDto>> {
        val response = try {
            greenSignalApi.getDepartmentList(page, perPage, token)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> {
                    return Resource.Error("Проверьте корректность введённых данных.")
                }
                401 -> {
                    prefs.edit()
                        .putString("inspector-jwt-token", null)
                        .apply()
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
            return Resource.Error("Что-то пошло не так\n" + e.message)
        }

        return Resource.Success(response)
    }

    override suspend fun getDepartment(id: String, token: String): Resource<DepartmentDto> {
        val response = try {
            greenSignalApi.getDepartment(id, token)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> {
                    return Resource.Error("Проверьте корректность введённых данных.")
                }
                401 -> {
                    prefs.edit()
                        .putString("inspector-jwt-token", null)
                        .apply()
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
            return Resource.Error("Что-то пошло не так\n" + e.message)
        }

        return Resource.Success(response)
    }
}