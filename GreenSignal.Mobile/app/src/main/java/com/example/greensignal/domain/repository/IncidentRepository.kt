package com.example.greensignal.domain.repository

import com.example.greensignal.data.remote.dto.request.CreateIncidentDto
import com.example.greensignal.data.remote.dto.response.IncidentDto
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentStatisticDto
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import com.example.greensignal.data.remote.dto.response.ReportType
import com.example.greensignal.domain.model.request.CreateIncident
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

@ActivityRetainedScoped
interface IncidentRepository {
    suspend fun getIncidentStatistic(): Resource<IncidentStatisticDto>
    suspend fun createIncident(createIncident: CreateIncident, token: String): Resource<IncidentDto>
    suspend fun applyIncident(id: String, token: String): Resource<IncidentDto>
    suspend fun getIncidents(page: Int?, perPage: Int?, isNerby: Boolean?, incidentKind: IncidentKind?, token: String): Resource<MutableList<IncidentDto>>
    suspend fun getInspectorIncidents(id: String, page: Int?, perPage: Int?, incidentKind: IncidentKind?, incidentStatus: IncidentStatus?, token: String): Resource<MutableList<IncidentDto>>
    suspend fun getIncident(id: String, token: String): Resource<IncidentDto>
    suspend fun getIncidentInWork(id: String, token: String): Resource<IncidentDto>
    suspend fun getIncidentsForCitizen(
        page: Int?,
        perPage: Int?,
        token: String
    ): Resource<MutableList<IncidentDto>>
    suspend fun reportIncident(id: String,
                               token: String,
                               reportType: ReportType
    ): Resource<String>
}