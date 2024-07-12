package com.example.greensignal.data.remote.dto.request

import com.example.greensignal.domain.model.request.UpdateIncidentReportAttribute
import com.example.greensignal.domain.model.request.UpdatePetitionAttribute

data class UpdatePetitionAttributeDto(
    val name: String = "",
    val stringValue: String? = null,
    val numberValue: Double? = null,
    val boolValue: Boolean? = null,
)

fun UpdatePetitionAttribute.toUpdatePetitionAttributeDto(): UpdatePetitionAttributeDto {
    return UpdatePetitionAttributeDto(
        name = name,
        stringValue = stringValue,
        numberValue = numberValue,
        boolValue = boolValue
    )
}