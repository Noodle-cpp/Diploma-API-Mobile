package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.IncidentStatisticDto

data class IncidentStatistic(
    val countOfIncidents: Int,
    val countOfCompletedIncidents: Int,
    val countOfIncidentsInWork: Int,
)

fun IncidentStatisticDto.toIncidentStatistic(): IncidentStatistic {
    return IncidentStatistic(
        countOfIncidents = countOfIncidents,
        countOfCompletedIncidents = countOfCompletedIncidents,
        countOfIncidentsInWork = countOfIncidentsInWork
    )
}
