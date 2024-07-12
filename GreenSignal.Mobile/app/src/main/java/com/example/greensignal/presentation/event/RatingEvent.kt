package com.example.greensignal.presentation.event

import java.time.LocalDateTime

sealed class RatingEvent{
    object GetRating: RatingEvent()
    object GetInspectorRating: RatingEvent()
    object GetScoreHistory: RatingEvent()
    data class TabChanged(val index: Int): RatingEvent()
}
