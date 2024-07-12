package com.example.greensignal.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class IncidentStatisticDto(
    @SerializedName("countOfIncidents")
    val countOfIncidents: Int,
    @SerializedName("countOfCompletedIncidents")
    val countOfCompletedIncidents: Int,
    @SerializedName("countOfIncidentsInWork")
    val countOfIncidentsInWork: Int
)
