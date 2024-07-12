package com.example.greensignal.data.repository

import com.example.greensignal.data.remote.GreenSignalApi
import com.example.greensignal.data.remote.dto.response.IncidentDto
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentStatisticDto
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import com.example.greensignal.data.remote.dto.response.ReportType
import com.example.greensignal.domain.model.request.CreateIncident
import com.example.greensignal.domain.repository.IncidentRepository
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class IncidentRepositoryImpl @Inject constructor(
    private val greenSignalApi: GreenSignalApi,
): IncidentRepository {
    override suspend fun getIncidentStatistic(): Resource<IncidentStatisticDto> {
        val response = try {
            greenSignalApi.getIncidentStatistic()
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

    override suspend fun createIncident(createIncident: CreateIncident, token: String): Resource<IncidentDto> {
        val response = try {
            greenSignalApi.createIncident(createIncident, token)
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

    override suspend fun applyIncident(id: String, token: String): Resource<IncidentDto> {
        val response = try {
            greenSignalApi.applyIncident(id, token)
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

    override suspend fun getIncidents(page: Int?, perPage: Int?, isNerby: Boolean?, incidentKind: IncidentKind?, token: String): Resource<MutableList<IncidentDto>> {
        val response = try {
            greenSignalApi.getIncidentsNerby(page, perPage, isNerby, incidentKind, token)
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

    override suspend fun getInspectorIncidents(id: String, page: Int?, perPage: Int?, incidentKind: IncidentKind?, incidentStatus: IncidentStatus?, token: String): Resource<MutableList<IncidentDto>> {
        val response = try {
            greenSignalApi.getIncidents(id, page, perPage, incidentKind, incidentStatus, token)
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

    override suspend fun getIncidentsForCitizen(page: Int?, perPage: Int?, token: String): Resource<MutableList<IncidentDto>> {
        val response = try {
            greenSignalApi.getIncidentsForCitizen(page, perPage, token)
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

    override suspend fun reportIncident(
        id: String,
        token: String,
        reportType: ReportType
    ): Resource<String> {
        try {
            greenSignalApi.reportIncident(id, token, reportType)
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

        return Resource.Success()
    }

    override suspend fun getIncident(id: String, token: String): Resource<IncidentDto> {
        val response = try {
            greenSignalApi.getIncident(id, token)
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

    override suspend fun getIncidentInWork(id: String, token: String): Resource<IncidentDto> {
        val response = try {
            greenSignalApi.getIncidentInWork(id, token)
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