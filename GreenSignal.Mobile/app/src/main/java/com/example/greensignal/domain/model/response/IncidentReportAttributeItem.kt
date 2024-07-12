package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.IncidentReportKind

data class IncidentReportAttributeItem(
    val name: String = "",
    val title: String = "",
    val type: String = "",
    val version: Int = 1,
    val isRequired: Boolean = false,
    val kind: IncidentReportKind = IncidentReportKind.AirPollution
)
