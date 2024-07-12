package com.example.greensignal.presentation.event

import android.content.Context
import com.example.greensignal.data.remote.dto.response.ReportType

sealed class IncidentEvent {
    data class GetIncident(val id: String): IncidentEvent()
    data class GetIncidentInWork(val id: String): IncidentEvent()
    data class ReportIncident(val type: ReportType): IncidentEvent()
}
