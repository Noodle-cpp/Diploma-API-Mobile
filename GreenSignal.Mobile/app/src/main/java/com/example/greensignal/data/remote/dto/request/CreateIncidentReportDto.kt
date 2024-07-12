package com.example.greensignal.data.remote.dto.request

import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.domain.model.request.CreateIncidentReport
import com.google.gson.annotations.SerializedName

data class CreateIncidentReportDto(
    @SerializedName("manualDate")
    val manualDate: String = "",
    @SerializedName("kind")
    val kind: Int = 0,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("startOfInspection")
    val startOfInspection: String = "",
    @SerializedName("endOfInspection")
    val endOfInspection: String = "",
    @SerializedName("address")
    val address: String = "",
    @SerializedName("incidentId")
    val incidentId: String? = null,
    @SerializedName("locationId")
    val locationId: String? = null,
    @SerializedName("lat")
    val lat: Double = 56.02,
    @SerializedName("lng")
    val lng: Double = 92.86,
    @SerializedName("attributesVersion")
    val attributesVersion: Int = 1,
)

fun CreateIncidentReport.toCreateIncidentReportDto(): CreateIncidentReportDto {
    return  CreateIncidentReportDto(
        manualDate = manualDate,
        kind = kind,
        description = description,
        startOfInspection = startOfInspection,
        endOfInspection = endOfInspection,
        address = address,
        incidentId = incidentId,
        locationId = locationId,
        lat = lat,
        lng = lng,
        attributesVersion = attributesVersion
    )
}