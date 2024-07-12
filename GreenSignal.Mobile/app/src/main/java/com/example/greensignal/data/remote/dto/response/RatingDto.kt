package com.example.greensignal.data.remote.dto.response

data class RatingDto(
    val place: Int = 0,
    val inspectorId: String = "",
    val inspector: InspectorDto? = null,
    val totalScore: Int = 0,
)
