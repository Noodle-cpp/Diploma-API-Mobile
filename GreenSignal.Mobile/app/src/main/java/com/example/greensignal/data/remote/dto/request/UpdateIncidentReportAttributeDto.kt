package com.example.greensignal.data.remote.dto.request

import com.example.greensignal.domain.model.request.UpdateIncidentReportAttribute

data class UpdateIncidentReportAttributeDto(
    val name: String = "",
    val stringValue: String? = null,
    val numberValue: Double? = null,
    val boolValue: Boolean? = null,
)

fun UpdateIncidentReportAttribute.toUpdateIncidentReportAttributeDto(): UpdateIncidentReportAttributeDto {
    return UpdateIncidentReportAttributeDto(
        name = name,
        stringValue = stringValue,
        numberValue = numberValue,
        boolValue = boolValue
    )
}

