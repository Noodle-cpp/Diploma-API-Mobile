package com.example.greensignal.data.remote.dto.response

data class IncidentReportAttributeDto(
    val id: String = "",
    val name: String = "",
    val stringValue: String? = null,
    val numberValue: Double? = null,
    val boolValue: Boolean? = null
)
