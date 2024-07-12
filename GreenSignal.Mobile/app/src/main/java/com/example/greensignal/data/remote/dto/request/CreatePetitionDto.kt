package com.example.greensignal.data.remote.dto.request

import com.example.greensignal.data.remote.dto.response.PetitionKind

data class CreatePetitionDto(
    val date: String = "",
    val departmentId: String = "",
    val incidentReportId: String? = null,
    val parentPetitionId: String? = null,
    val kind: Int = 0,
    val attributeVersion: Int = 1,
)
