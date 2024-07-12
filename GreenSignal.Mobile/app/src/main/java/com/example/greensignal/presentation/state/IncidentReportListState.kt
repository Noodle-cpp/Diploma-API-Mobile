package com.example.greensignal.presentation.state

import androidx.paging.PagingData
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus
import com.example.greensignal.domain.model.response.IncidentReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class IncidentReportListState(
    val address: String = "",
    val selectedIncidentReportKind: IncidentReportKind? = null,
    val selectedIncidentReportStatus: IncidentReportStatus? = null,

    val incidentReports: Flow<PagingData<IncidentReport>> = emptyFlow(),

    val page: Int? = null,
    val perPage: Int? = null,

    val isLoading: Boolean = false,
    val error: String? = null
)
