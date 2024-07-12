package com.example.greensignal.data.remote.dto.request

data class UpdatePetitionDto(
    val date: String = "",
    val departmentId: String = "",
    val incidentReportId: String? = null,
    val parentPetitionId: String? = null,
    val kind: Int = 0,
    val attributeVersion: Int = 1,
)
