package com.example.greensignal.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class IncidentReportStatisticDto(
    @SerializedName("countOfSentIncidentReports")
    val countOfSentIncidentReports: Int,
)
