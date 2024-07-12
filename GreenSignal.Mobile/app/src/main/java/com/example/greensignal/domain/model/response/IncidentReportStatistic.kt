package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.IncidentReportStatisticDto

data class IncidentReportStatistic(
    val countOfSentIncidentReports: Int,
)

fun IncidentReportStatisticDto.toIncidentReportStatistic(): IncidentReportStatistic {
    return  IncidentReportStatistic(
        countOfSentIncidentReports = countOfSentIncidentReports
    )
}