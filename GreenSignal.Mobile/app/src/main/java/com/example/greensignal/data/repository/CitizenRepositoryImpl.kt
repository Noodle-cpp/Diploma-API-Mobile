package com.example.greensignal.data.repository

import android.app.Application
import android.content.SharedPreferences
import com.example.greensignal.data.remote.GreenSignalApi
import com.example.greensignal.data.remote.dto.request.AuthorizeCitizenDto
import com.example.greensignal.data.remote.dto.response.TokenDto
import com.example.greensignal.domain.model.request.GetCode
import com.example.greensignal.domain.repository.CitizenRepository
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class CitizenRepositoryImpl @Inject constructor(
    private val greenSignalApi: GreenSignalApi,
    private val appContext: Application,
    private val prefs: SharedPreferences
) : CitizenRepository {

    override suspend fun citizenGetCode(getCode: GetCode): Resource<String> {
        try {
            greenSignalApi.getCitizenCode(getCode)
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
            return Resource.Error(e.message() ?: "Что-то пошло не так.")
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету.")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так.")
        }

        return Resource.Success()
    }

    override suspend fun citizenLogin(authorizeCitizenDto: AuthorizeCitizenDto): Resource<TokenDto> {
        val response = try {
            greenSignalApi.citizenLogin(authorizeCitizenDto)
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
            return Resource.Error(e.message() ?: "Что-то пошло не так.")
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету.")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так.")
        }

        prefs.edit()
            .putString("citizen-jwt-token", response.token)
            .apply()

        return Resource.Success(response)
    }

}