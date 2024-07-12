package com.example.greensignal.presentation.state

import androidx.paging.PagingData
import com.example.greensignal.domain.model.response.Incident
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class HomeState (
    val countOfSentIncidentReports: Int = 0,
    val countOfCompletedIncidents: Int = 0,
    val countOfIncidents: Int = 0,
    val countOfIncidentsInWork: Int = 0,

    val isAuthorized: Boolean = false,

    val incidents: Flow<PagingData<Incident>> = emptyFlow(),

    var error: String? = null,
    var isLoading: Boolean = false,
    var errorPagination: String? = null,
    var isLoadingPagination: Boolean = false,
    var isCitizenAuthorized: Boolean = false,
)