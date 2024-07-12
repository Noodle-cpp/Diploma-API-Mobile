package com.example.greensignal.data.repository

import android.app.Application
import android.content.SharedPreferences
import com.example.greensignal.data.remote.GreenSignalApi
import com.example.greensignal.data.remote.dto.request.UpdateInspectorDto
import com.example.greensignal.data.remote.dto.request.toAuthorizeInspectorDto
import com.example.greensignal.data.remote.dto.request.toGetCodeDto
import com.example.greensignal.data.remote.dto.request.toUpdateInspectorLocationDto
import com.example.greensignal.data.remote.dto.response.InspectorDto
import com.example.greensignal.data.remote.dto.response.MessageDto
import com.example.greensignal.data.remote.dto.response.RatingDto
import com.example.greensignal.data.remote.dto.response.ScoreDto
import com.example.greensignal.data.remote.dto.response.SessionDto
import com.example.greensignal.data.remote.dto.response.TokenDto
import com.example.greensignal.domain.model.request.AuthorizeInspector
import com.example.greensignal.domain.model.request.GetCode
import com.example.greensignal.domain.model.request.UpdateInspectorLocation
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class InspectorRepostoryImpl @Inject constructor(
    private val greenSignalApi: GreenSignalApi,
    private val appContext: Application,
    private val prefs: SharedPreferences
): InspectorRepository {

    override suspend fun inspectorLogin(authorizeInspector: AuthorizeInspector): Resource<TokenDto> {
        val response = try {
            greenSignalApi.inspectorLogin(authorizeInspector.toAuthorizeInspectorDto())
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
            return Resource.Error("Что-то пошло не так")
        }

        prefs.edit()
            .putString("inspector-jwt-token", response.token)
            .apply()

        return Resource.Success(response)
    }

    override suspend fun inspectorGetCode(getCode: GetCode): Resource<String> {
        try {
            greenSignalApi.getInspectorCode(getCode.toGetCodeDto())
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
            return Resource.Error(e.message() ?: "Что-то пошло не так.")
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету.")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так.")
        }

        return Resource.Success()
    }

    override suspend fun inspectorLogout(token: String): Resource<String> {
        try {
            greenSignalApi.inspectorLogout(token)
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
            return Resource.Error(e.message() ?: "Что-то пошло не так.")
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету.")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так.")
        }

        return Resource.Success()
    }

    override suspend fun inspectorGetProfile(token: String): Resource<InspectorDto> {
        val response = try {
            greenSignalApi.getInspectorProfile(token)
        } catch (e: HttpException) {
            when(e.code()) {
                400 -> {
                    return Resource.Error("Проверьте корректность введённых данных.", code = 400)
                }
                401 -> {
                    prefs.edit()
                        .putString("inspector-jwt-token", null)
                        .apply()
                    return Resource.Error("Введены некорректные данные.", code = 401)
                }
                403 -> {
                    return Resource.Error("Отказано в доступе.", code = 403)
                }
                404 -> {
                    return Resource.Error("Информация не найдена.", code = 404)
                }
                422 -> {
                    return Resource.Error("Проверьте корректность введённых данных.", code = 422)
                }
                429 -> {
                    return Resource.Error("Превышен лимит запросов. Попробуйте позже.", code = 429)
                }
            }
            return Resource.Error(e.message() ?: "Что-то пошло не так.", code = 500)
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету.")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так.")
        }

        return Resource.Success(response)
    }

    override suspend fun updateInspectorLocation(token: String, updateInspectorLocation: UpdateInspectorLocation): Resource<String> {
        try {
            greenSignalApi.updateInspectorLocation(token, updateInspectorLocation.toUpdateInspectorLocationDto())
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
            return Resource.Error(e.message() ?: "Что-то пошло не так.")
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету.")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так.")
        }

        return Resource.Success()
    }

    override suspend fun createInspector(
        fio: RequestBody,
        phone: RequestBody,
        certificateId: RequestBody,
        certificateDate: RequestBody,
        schoolId: RequestBody,
        code: RequestBody,
        firebaseToken: RequestBody,
        deviceName: RequestBody,
        inspectorPhoto: MultipartBody.Part,
        certificatePhoto: MultipartBody.Part
    ): Resource<TokenDto> {
        val response = try {
            greenSignalApi.createInspector(
                fio,
                phone,
                certificateId,
                certificateDate,
                schoolId,
                code,
                firebaseToken,
                deviceName,
                inspectorPhoto,
                certificatePhoto
            )
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

    override suspend fun updateInspector(
        id: String,
        updateInspector: UpdateInspectorDto,
        token: String
    ): Resource<InspectorDto> {
        val response = try {
            greenSignalApi.updateInspector(
                id,
                updateInspector,
                token
            )
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

    override suspend fun updateInspectorPhoto(
        id: String,
        inspectorPhoto: MultipartBody.Part,
        token: String
    ): Resource<InspectorDto> {
        val response = try {
            greenSignalApi.updateInspectorPhoto(id, inspectorPhoto, token)
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
            return Resource.Error(e.message() ?: "Что-то пошло не так.")
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету.")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так.")
        }

        return Resource.Success(response)
    }

    override suspend fun updateInspectorSignature(
        id: String,
        signaturePhoto: MultipartBody.Part,
        token: String
    ): Resource<InspectorDto> {
        val response = try {
            greenSignalApi.updateInspectorSignature(id, signaturePhoto, token)
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
            return Resource.Error(e.message() ?: "Что-то пошло не так.")
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету.")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так.")
        }

        return Resource.Success(response)
    }

    override suspend fun updateInspectorCert(
        id: String,
        inspectorCert: MultipartBody.Part,
        token: String
    ): Resource<InspectorDto> {
        val response = try {
            greenSignalApi.updateInspectorCert(id, inspectorCert, token)
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
            return Resource.Error(e.message() ?: "Что-то пошло не так.")
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету.")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так.")
        }

        return Resource.Success(response)
    }

    override suspend fun registrationInspectorGetCode(getCode: GetCode): Resource<String> {
        try {
            greenSignalApi.registrationInspectorGetCode(getCode.toGetCodeDto())
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
            return Resource.Error(e.message() ?: "Что-то пошло не так.")
        } catch (e: IOException) {
            return Resource.Error("Пожалуйста, проверьте ваше подключение к интернету.")
        } catch (e: Exception) {
            return Resource.Error("Что-то пошло не так.")
        }

        return Resource.Success()
    }

    override suspend fun getRating(
        startDate: String?,
        endDate: String?,
        token: String
    ): Resource<MutableList<RatingDto>> {
        val response = try {
            greenSignalApi.getRating(startDate, endDate, token)
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

    override suspend fun getInspectorRating(
        id: String,
        startDate: String?,
        endDate: String?,
        token: String
    ): Resource<RatingDto> {
        val response = try {
            greenSignalApi.getInspectorRating(id, startDate = startDate, endDate =  endDate, token =  token)
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

    override suspend fun getScoreHistory(
        id: String,
        startDate: String?,
        endDate: String?,
        page: Int?,
        perPage: Int?,
        token: String
    ): Resource<MutableList<ScoreDto>> {
        val response = try {
            greenSignalApi.getScoreHistory(id, startDate = startDate, endDate =  endDate, page =  page, perPage =  perPage, token = token)
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

    override suspend fun getInspectorMessages(
        inspectorId: String,
        filter: String?,
        token: String
    ): Resource<MutableList<MessageDto>> {
        val response = try {
            greenSignalApi.getInspectorMessages(inspectorId, token, filter)
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

    override suspend fun getMessage(
        id: String,
        messageId: String,
        token: String
    ): Resource<MessageDto> {
        val response = try {
            greenSignalApi.getMessage(id, messageId, token)
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

    override suspend fun setMessageAsSeen(
        id: String,
        messageId: String,
        token: String
    ): Resource<MessageDto> {
        val response = try {
            greenSignalApi.setMessageAsSeen(id, messageId, token)
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

    override suspend fun getSessions(id: String, token: String): Resource<MutableList<SessionDto>> {
        val response = try {
            greenSignalApi.getSessions(id, token)
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

    override suspend fun deleteSessions(
        id: String,
        inspectorId: String,
        token: String
    ): Resource<String> {
        try {
            greenSignalApi.deleteSessions(id, inspectorId, token)
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
}