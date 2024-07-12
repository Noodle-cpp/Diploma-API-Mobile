package com.example.greensignal.data.remote.dto.request

import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.domain.model.request.CreateIncident
import com.google.gson.annotations.SerializedName

data class CreateIncidentDto (
    @SerializedName("description")
    val description: String = "",
    @SerializedName("address")
    val address: String = "",
    @SerializedName("lat")
    val lat: Double = 56.02,
    @SerializedName("lng")
    val lng: Double = 92.86,
    @SerializedName("kind")
    val kind: IncidentKind = IncidentKind.Dump,
)

fun CreateIncident.toCreateIncidentDto(): CreateIncidentDto {
    return CreateIncidentDto(
        description = description,
        address = address,
        lat = lat,
        lng = lng,
        kind = IncidentKind.values().firstOrNull { it.index == kind } ?: IncidentKind.Dump
    )
}