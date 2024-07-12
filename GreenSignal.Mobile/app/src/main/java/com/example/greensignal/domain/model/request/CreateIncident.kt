package com.example.greensignal.domain.model.request

import com.example.greensignal.data.remote.dto.request.CreateIncidentDto
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.InspectorStatus

data class CreateIncident(
    val description: String = "",
    val address: String = "",
    val lat: Double = 56.02,
    val lng: Double = 92.86,
    val kind: Int = 0,
)

