package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.IncidentReportAttributeDto

data class IncidentReportAttribute(
    val id: String = "",
    val name: String = "",
    val stringValue: String? = null,
    val numberValue: Double? = null,
    val boolValue: Boolean? = null
)

fun IncidentReportAttributeDto.toIncidentReportAttribute(): IncidentReportAttribute {
    return IncidentReportAttribute(
        id = id,
        name = name,
        stringValue = stringValue,
        numberValue = numberValue,
        boolValue = boolValue
    )
}
