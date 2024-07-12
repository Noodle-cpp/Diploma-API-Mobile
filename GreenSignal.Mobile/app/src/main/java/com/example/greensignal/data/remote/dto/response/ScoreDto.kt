package com.example.greensignal.data.remote.dto.response

import java.time.LocalDateTime

data class ScoreDto(
    val id: String = "",
    val score: Int = 0,
    val date: String = "",
    val type: ScoreType = ScoreType.Custom,
    val comment: String = ""
)

enum class ScoreType(
    val index: Int,
    val title: String
) {
    TakeIncident(100, "Взятие нарушения"),
    PetitionSent(150, "Отправление обращения"),
    PetitionClose(200, "Закрытие обращения"),
    OverdueIncident(-150, "Просроченное нарушение"),
    Custom(0, "Пользовательские очки")
}