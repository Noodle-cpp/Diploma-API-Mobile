package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.ScoreDto
import com.example.greensignal.data.remote.dto.response.ScoreType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Score(
    val id: String = "",
    val score: Int = 0,
    val date: String = "",
    val type: ScoreType = ScoreType.Custom,
    val comment: String = ""
)

fun ScoreDto.toScore(): Score {
    return Score (
        id = id,
        score = score,
        date = date,
        type = type,
        comment = comment
    )
}
