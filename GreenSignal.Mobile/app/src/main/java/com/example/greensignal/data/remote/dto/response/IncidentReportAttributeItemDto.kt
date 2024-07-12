package com.example.greensignal.data.remote.dto.response

import com.example.greensignal.domain.model.response.IncidentReportAttributeItem

data class IncidentReportAttributeItemDto(
    val name: String = "",
    val title: String = "",
    val type: String = "",
    val version: Int = 1,
    val isRequired: Boolean = false,
    val kind: IncidentReportKind = IncidentReportKind.AirPollution
)

fun IncidentReportAttributeItem.toIncidentReportAttributeItem(): IncidentReportAttributeItem {
    return IncidentReportAttributeItem(
        name = name,
        title = title,
        type = type,
        version = version,
        isRequired = isRequired,
        kind = kind
    )
}