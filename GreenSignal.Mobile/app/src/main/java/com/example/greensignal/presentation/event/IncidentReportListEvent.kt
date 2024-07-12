package com.example.greensignal.presentation.event

import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus

sealed class IncidentReportListEvent {
    data class IsSelectedIncidentReportKindChanged(val incidentReportKind: IncidentReportKind?): IncidentReportListEvent()
    data class IsSelectedIncidentReportStatusChanged(val incidentReportStatus: IncidentReportStatus?): IncidentReportListEvent()
    data class AddressChanged(val address: String): IncidentReportListEvent()
    object GetIncidentReportList: IncidentReportListEvent()
}
