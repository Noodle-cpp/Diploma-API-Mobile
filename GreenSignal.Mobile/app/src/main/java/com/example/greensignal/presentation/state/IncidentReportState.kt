package com.example.greensignal.presentation.state

import androidx.compose.runtime.mutableStateListOf
import com.example.greensignal.domain.model.response.IncidentReport
import com.google.android.gms.maps.model.LatLng

data class IncidentReportState(
    val incidentReport: IncidentReport = IncidentReport(),
    val points:List<LatLng> = mutableStateListOf(),

    val isLoading: Boolean = false,
    val error: String? = null,
    val isRemoveLoading: Boolean = false,
    val removeError: String? = null
)
