package com.example.greensignal.domain.model.request

import com.example.greensignal.data.remote.dto.request.CreateIncidentReportDto
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.google.gson.annotations.SerializedName

data class CreateIncidentReport(
    val manualDate: String = "",
    val kind: Int = 0,
    val description: String = "",
    val startOfInspection: String = "",
    val endOfInspection: String = "",
    val address: String = "",
    val incidentId: String? = null,
    val locationId: String? = null,
    val lat: Double = 56.02,
    val lng: Double = 92.86,
    val attributesVersion: Int = 1,
)

