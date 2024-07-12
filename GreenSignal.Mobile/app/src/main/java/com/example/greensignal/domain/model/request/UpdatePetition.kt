package com.example.greensignal.domain.model.request

import com.example.greensignal.data.remote.dto.request.UpdatePetitionDto

data class UpdatePetition(
    val date: String = "",
    val departmentId: String = "",
    val incidentReportId: String? = null,
    val parentPetitionId: String? = null,
    val kind: Int = 0,
    val attributeVersion: Int = 1,
)

fun UpdatePetition.toUpdatePetitionDto(): UpdatePetitionDto {
    return UpdatePetitionDto(
        date = date,
        departmentId = departmentId,
        incidentReportId = incidentReportId,
        parentPetitionId = parentPetitionId,
        kind = kind,
        attributeVersion = attributeVersion
    )
}
