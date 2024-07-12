package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.RatingDto

data class Rating (
    val place: Int = 0,
    val inspectorId: String = "",
    val inspector: Inspector? = null,
    val totalScore: Int = 0,
)

fun RatingDto.toRating(): Rating {
    return Rating(
        place = place,
        inspectorId = inspectorId,
        inspector = inspector?.toInspectorAccount(),
        totalScore = totalScore
    )
}