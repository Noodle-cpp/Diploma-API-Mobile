package com.example.greensignal.presentation.event

sealed class HomeEvent {
    object GetIncidentStatistic: HomeEvent()
    object GetIncidentReportStatistic: HomeEvent()
    object GetIncidents: HomeEvent()
    object CheckToken: HomeEvent()
}