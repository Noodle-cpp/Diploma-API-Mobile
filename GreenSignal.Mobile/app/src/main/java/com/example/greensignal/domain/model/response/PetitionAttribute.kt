package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.PetitionAttributeDto

data class PetitionAttribute(
    val id: String = "",
    val name: String = "",
    val stringValue: String? = null,
    val numberValue: Double? = null,
    val boolValue: Boolean? = null
)

fun PetitionAttributeDto.toPetitionAttribute(): PetitionAttribute{
    return PetitionAttribute(
        id = id,
        name = name,
        stringValue = stringValue,
        numberValue = numberValue,
        boolValue = boolValue
    )
}