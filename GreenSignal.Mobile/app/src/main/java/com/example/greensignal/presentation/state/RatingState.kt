package com.example.greensignal.presentation.state

import com.example.greensignal.domain.model.response.Rating
import com.example.greensignal.domain.model.response.Score
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneOffset

data class RatingState (
    val startDate: String? = null,
    val endDate: String? = null,
    val rating: MutableList<Rating> = mutableListOf(),
    val currentInspectorScore: Rating = Rating(),
    val page: Int? = null,
    val perPage: Int? = null,

    val scoreHistory: MutableList<Score> = mutableListOf(),

    val tabOptions: List<String> = listOf("За месяц", "За год", "За всё время"),
    val selectedTabIndex: Int = 0,

    val isLoading: Boolean = false,
    val error: String? = null
)