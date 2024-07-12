package com.example.greensignal.presentation.state

import androidx.paging.PagingData
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import com.example.greensignal.domain.model.response.Incident
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class IncidentListState (
        val tabOptions: List<String> = listOf("Нарушения", "Нарушения в работе"),
        val selectedTabIndex: Int = 0,

        val isNerby: Boolean = true,
        val address: String = "",
        val selectedIncidentKind: IncidentKind? = null,
        val selectedIncidentStatus: IncidentStatus? = null,

        val incidents: Flow<PagingData<Incident>> = emptyFlow(),

        val page: Int? = null,
        val perPage: Int? = null,

        val isLoading: Boolean = false,
        val error: String? = null
    )