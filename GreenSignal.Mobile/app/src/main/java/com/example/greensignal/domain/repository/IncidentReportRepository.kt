package com.example.greensignal.domain.repository

import com.example.greensignal.data.remote.dto.request.CreateIncidentReportDto
import com.example.greensignal.data.remote.dto.request.UpdateIncidentReportAttributeDto
import com.example.greensignal.data.remote.dto.request.UpdateIncidentReportDto
import com.example.greensignal.data.remote.dto.response.IncidentReportAttributeItemDto
import com.example.greensignal.data.remote.dto.response.IncidentReportDto
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatisticDto
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

@ActivityRetainedScoped
interface IncidentReportRepository {
    suspend fun getIncidentReportStatistic(): Resource<IncidentReportStatisticDto>
    suspend fun getIncidentReportAttributeItem(incidentReportKind: IncidentReportKind,
                                               attributeVersion: Int,
                                               token: String): Resource<MutableList<IncidentReportAttributeItemDto>>
    suspend fun createIncidentReport(createIncidentReportDto: CreateIncidentReportDto,
                                     token: String): Resource<IncidentReportDto>
    suspend fun attachFileToIncidentReport(multipartImage: MultipartBody.Part,
                                           description: RequestBody,
                                           manualDate: RequestBody,
                                           id: String,
                                           token: String) : Resource<String>
    suspend fun updateIncidentReportAttributes(
        updateIncidentReportAttributes: MutableList<UpdateIncidentReportAttributeDto>,
        id: String,
        token: String
    ): Resource<IncidentReportDto>

    suspend fun getIncidentReport(
        id: String,
        token: String
    ): Resource<IncidentReportDto>

    suspend fun getIncidentReportList(
        page: Int?,
        perPage: Int?,
        incidentReportKind: IncidentReportKind?,
        incidentReportStatus: IncidentReportStatus?,
        token: String
    ): Resource<MutableList<IncidentReportDto>>

    suspend fun getIncidentReportPdf(
        id: String,
        token: String
    ): Resource<ResponseBody>

    suspend fun deleteIncidentReport(
        id: String,
        token: String
    ): Resource<ResponseBody>

    suspend fun detachAttachmentIncidentReport(id: String,
                                               incidentReportId: String,
                                               token: String): Resource<IncidentReportDto>

    suspend fun updateIncidentReport(id: String,
                                     updateIncidentReport: UpdateIncidentReportDto,
                                     token: String): Resource<IncidentReportDto>
}