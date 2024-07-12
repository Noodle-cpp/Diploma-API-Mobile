package com.example.greensignal.presentation.state

import com.example.greensignal.domain.model.response.Incident

data class IncidentState(
    val incident: Incident = Incident(),
    val formattedPhone: String = "",
    val inspectorId: String = "",

    val isLoading: Boolean = false,
    val error: String? = null
)
