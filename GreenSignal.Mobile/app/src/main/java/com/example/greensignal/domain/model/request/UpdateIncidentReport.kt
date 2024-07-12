package com.example.greensignal.domain.model.request

data class UpdateIncidentReport(
    val manualDate: String = "",
    val kind: Int = 0,
    val startOfInspection: String = "",
    val endOfInspection: String = "",
    val description: String = "",
    val address: String = "",
    val incidentId: String? = null,
    val locationId: String? = null,
    val lat: Double = 56.02,
    val lng: Double = 92.86,
    val attributesVersion: Int = 1,
)
