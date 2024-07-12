package com.example.greensignal.data.repository

import android.app.Application
import android.content.SharedPreferences
import com.example.greensignal.data.remote.GreenSignalApi
import com.example.greensignal.data.remote.dto.request.CreateIncidentReportDto
import com.example.greensignal.data.remote.dto.request.UpdateIncidentReportAttributeDto
import com.example.greensignal.data.remote.dto.request.UpdateIncidentReportDto
import com.example.greensignal.data.remote.dto.response.IncidentReportAttributeItemDto
import com.example.greensignal.data.remote.dto.response.IncidentReportDto
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatisticDto
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus
import com.example.greensignal.domain.repository.IncidentReportRepository
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class IncidentReportRepositoryImpl @Inject constructor(
    private val greenSignalApi: GreenSignalApi,
    private val appContext: Application,
    private val prefs: SharedPreferences
): IncidentReportRepository {
    override suspend fun getIncidentReportStatistic(): Resource<IncidentReportStatisticDto> {
        val response = try {
            greenSignalApi.getIncidentReportsStatistic()
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
            return Resource.Error("Что-то пошло не так")
        }

        return Resource.Success(response)
    }

    override suspend fun getIncidentReportAttributeItem(
        incidentReportKind: IncidentReportKind,
        attributeVersion: Int,
        token: String
    ): Resource<MutableList<IncidentReportAttributeItemDto>> {
        val response = try {
            greenSignalApi.getIncidentReportAttributesItem(incidentReportKind, attributeVersion, token)
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
            return Resource.Error("Что-то пошло не так")
        }

        return Resource.Success(response)
    }

    override suspend fun createIncidentReport(createIncidentReportDto: CreateIncidentReportDto, token: String): Resource<IncidentReportDto> {
        val response = try {
            greenSignalApi.createIncidentReport(createIncidentReportDto, token)
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
            return Resource.Error("Что-то пошло не так")
        }

        return Resource.Success(response)
    }

    override suspend fun attachFileToIncidentReport(
        multipartImage: MultipartBody.Part,
        description: RequestBody,
        manualDate: RequestBody,
        id: String,
        token: String
    ): Resource<String> {
        try {
            greenSignalApi.attachmentAddIncidentReport(id, token, multipartImage, description, manualDate)
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

    override suspend fun updateIncidentReportAttributes(
        updateIncidentReportAttributes: MutableList<UpdateIncidentReportAttributeDto>,
        id: String,
        token: String
    ): Resource<IncidentReportDto> {
        val response = try {
            greenSignalApi.updateIncidentReportAttributes(updateIncidentReportAttributes, id, token)
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

        return Resource.Success(response)
    }

    override suspend fun getIncidentReport(id: String, token: String): Resource<IncidentReportDto> {
        val response = try {
            greenSignalApi.getIncidentReport(id, token)
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

        return Resource.Success(response)
    }

    override suspend fun getIncidentReportList(
        page: Int?,
        perPage: Int?,
        incidentReportKind: IncidentReportKind?,
        incidentReportStatus: IncidentReportStatus?,
        token: String
    ): Resource<MutableList<IncidentReportDto>> {
        val response = try {
            greenSignalApi.getIncidentReportList(page, perPage, incidentReportKind, incidentReportStatus, token)
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

        return Resource.Success(response)
    }

    override suspend fun getIncidentReportPdf(
        id: String,
        token: String
    ): Resource<ResponseBody> {
        val response = try {
            greenSignalApi.getIncidentReportPdf(id, token)
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

        return Resource.Success(response)
    }

    override suspend fun deleteIncidentReport(id: String, token: String): Resource<ResponseBody> {
        val response = try {
            greenSignalApi.deleteIncidentReport(id, token)
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

        return Resource.Success(response)
    }

    override suspend fun detachAttachmentIncidentReport(
        id: String,
        incidentReportId: String,
        token: String
    ): Resource<IncidentReportDto> {
        val response = try {
            greenSignalApi.detachAttachmentIncidentReport(id, incidentReportId, token)
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

        return Resource.Success(response)
    }

    override suspend fun updateIncidentReport(
        id: String,
        updateIncidentReport: UpdateIncidentReportDto,
        token: String
    ): Resource<IncidentReportDto> {
        val response = try {
            greenSignalApi.updateIncidentReport(id, updateIncidentReport, token)
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

        return Resource.Success(response)
    }
}