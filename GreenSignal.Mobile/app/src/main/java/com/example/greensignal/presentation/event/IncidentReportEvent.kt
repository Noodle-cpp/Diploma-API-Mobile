package com.example.greensignal.presentation.event

import android.content.Context
import com.example.greensignal.domain.model.response.SavedFile

sealed class IncidentReportEvent {
    data class GetIncidentReport(val id: String): IncidentReportEvent()
    data class DownloadPdf(val context: Context): IncidentReportEvent()
    object ArchiveIncidentReport: IncidentReportEvent()
}