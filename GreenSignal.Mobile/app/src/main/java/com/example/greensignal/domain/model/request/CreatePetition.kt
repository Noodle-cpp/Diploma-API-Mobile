package com.example.greensignal.domain.model.request

import com.example.greensignal.data.remote.dto.request.CreatePetitionDto
import com.example.greensignal.data.remote.dto.response.PetitionKind

data class CreatePetition(
    val date: String = "",
    val departmentId: String = "",
    val incidentReportId: String? = null,
    val parentPetitionId: String? = null,
    val kind: Int = 0,
    val attributeVersion: Int = 1,
)

fun CreatePetition.toCreatePetitionDto(): CreatePetitionDto {
    return CreatePetitionDto(
        date = date,
        departmentId = departmentId,
        incidentReportId = incidentReportId,
        parentPetitionId = parentPetitionId,
        kind = kind,
        attributeVersion = attributeVersion
    )
}
