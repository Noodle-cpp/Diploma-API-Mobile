package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import com.example.greensignal.domain.model.response.Incident
import com.example.greensignal.domain.repository.IncidentRepository
import com.example.greensignal.presentation.event.IncidentListEvent
import com.example.greensignal.presentation.state.IncidentListState
import com.example.greensignal.presentation.use_case.pagination.IncidentListPagination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class IncidentListViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val prefs: SharedPreferences
    ): ViewModel() {
    var state by mutableStateOf(IncidentListState())
        private set

    private val validationEventChannel = Channel<IncidentListViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        getIncidentPagination()
    }

    fun onEvent(event: IncidentListEvent) {
        when(event) {
            is IncidentListEvent.TabChanged -> {
                state = state.copy(selectedTabIndex = event.index)
                getIncidentPagination()
            }

            is IncidentListEvent.IsNerbyChanged -> {
                state = state.copy(isNerby = event.isChecked)
                getIncidentPagination()
            }

            is IncidentListEvent.IsSelectedIncidentKindChanged -> {
                state = state.copy(selectedIncidentKind = event.incidentKind)
                getIncidentPagination()
            }

            is IncidentListEvent.IsSelectedIncidentStatusChanged -> {
                state = state.copy(selectedIncidentStatus = event.incidentStatus)
                getIncidentPagination()
            }

            is IncidentListEvent.AddressChanged -> {
                state = state.copy(address = event.address)
            }

            IncidentListEvent.GetIncidentList -> {
                getIncidentPagination()
            }
        }
    }

    private fun getIncidentPagination() {
        state = state.copy(incidents = Pager(PagingConfig(pageSize = 5)) {
            IncidentListPagination(incidentRepository,
                                    prefs,
                                    state.selectedIncidentKind,
                                    if(state.selectedTabIndex == 0) IncidentStatus.Submitted else state.selectedIncidentStatus,
                                    state.selectedTabIndex,
                                    state.isNerby)
        }.flow)
    }

    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }
}