package com.example.greensignal.presentation.state

import androidx.compose.runtime.mutableStateListOf
import com.example.greensignal.domain.model.response.Rating
import com.example.greensignal.domain.model.response.Session

data class SessionListState(
    val sessions: MutableList<Session> = mutableListOf(),

    val isLoading: Boolean = false,
    val error: String? = null
)
