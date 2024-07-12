package com.example.greensignal.data.repository

import android.content.SharedPreferences
import com.example.greensignal.data.remote.GreenSignalApi
import com.example.greensignal.data.remote.dto.request.CreatePetitionDto
import com.example.greensignal.data.remote.dto.request.UpdatePetitionAttributeDto
import com.example.greensignal.data.remote.dto.request.UpdatePetitionDto
import com.example.greensignal.data.remote.dto.response.PetitionDto
import com.example.greensignal.domain.repository.PetitionRepository
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class PetitionRepositoryImpl @Inject constructor(
    private val greenSignalApi: GreenSignalApi,
    private val prefs: SharedPreferences
): PetitionRepository {
    override suspend fun createPetition(
        createPetition: CreatePetitionDto,
        token: String
    ): Resource<PetitionDto> {
        val response = try {
            greenSignalApi.createPetition(createPetition, token)
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

    override suspend fun getPetition(id: String, token: String): Resource<PetitionDto> {
        val response = try {
            greenSignalApi.getPetition(id, token)
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

    override suspend fun getPetitionList(
        page: Int?,
        perPage: Int?,
        token: String,
    ): Resource<MutableList<PetitionDto>> {
        val response = try {
            greenSignalApi.getPetitionList(page, perPage, token)
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

    override suspend fun updatePetitionAttribute(
        id: String,
        updatePetitionAttributes: MutableList<UpdatePetitionAttributeDto>,
        token: String
    ): Resource<String> {
        try {
            greenSignalApi.updatePetitionAttribute(id, updatePetitionAttributes, token)
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

        return Resource.Success()
    }

    override suspend fun removePetition(id: String, token: String): Resource<String> {
        try {
            greenSignalApi.removePetition(id, token)
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

        return Resource.Success()
    }

    override suspend fun attachMessageToPetition(
        id: String,
        receiveMessageId: String,
        token: String
    ): Resource<String> {
        try {
            greenSignalApi.attachMessageToPetition(id, receiveMessageId, token)
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

        return Resource.Success()
    }

    override suspend fun detachMessageFromPetition(
        id: String,
        receiveMessageId: String,
        token: String
    ): Resource<String> {
        try {
            greenSignalApi.detachMessageToPetition(id, receiveMessageId, token)
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

        return Resource.Success()
    }

    override suspend fun closeSuccessPetition(id: String, token: String): Resource<String> {
        try {
            greenSignalApi.closeSuccessPetition(id, token)
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

        return Resource.Success()
    }

    override suspend fun sentPetition(id: String, token: String): Resource<String> {
        try {
            greenSignalApi.sentPetition(id, token)
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

        return Resource.Success()
    }

    override suspend fun closeFailedPetition(id: String, token: String): Resource<String> {
        try {
            greenSignalApi.closeFailedPetition(id, token)
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

        return Resource.Success()
    }

    override suspend fun updatePetition(
        id: String,
        updatePetitionDto: UpdatePetitionDto,
        token: String
    ): Resource<PetitionDto> {
        val response = try {
            greenSignalApi.updatePetition(id, updatePetitionDto, token)
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