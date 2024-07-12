package com.example.greensignal.data.remote.dto.request

import com.example.greensignal.domain.model.request.UpdateIncidentReport
import kotlin.math.ln

data class UpdateIncidentReportDto(
    val manualDate: String = "",
    val kind: Int = 0,
    val startOfInspection: String = "",
    val endOfInspection: String = "",
    val address: String = "",
    val description: String = "",
    val incidentId: String? = null,
    val locationId: String? = null,
    val lat: Double = 56.02,
    val lng: Double = 92.86,
    val attributesVersion: Int = 1,
)

fun UpdateIncidentReport.toUpdateIncidentReportDto(): UpdateIncidentReportDto{
    return UpdateIncidentReportDto(
        manualDate = manualDate,
        kind = kind,
        startOfInspection = startOfInspection,
        endOfInspection = endOfInspection,
        address = address,
        description = description,
        incidentId = incidentId,
        locationId = locationId,
        lat = lat,
        lng = lng,
        attributesVersion = attributesVersion
    )
}