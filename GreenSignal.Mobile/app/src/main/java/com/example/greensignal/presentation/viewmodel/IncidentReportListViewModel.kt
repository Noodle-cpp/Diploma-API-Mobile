package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.greensignal.domain.repository.IncidentReportRepository
import com.example.greensignal.presentation.event.IncidentReportListEvent
import com.example.greensignal.presentation.state.IncidentReportListState
import com.example.greensignal.presentation.use_case.pagination.IncidentReportListPagination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class IncidentReportListViewModel @Inject constructor(
    private val incidentReportRepository: IncidentReportRepository,
    private val prefs: SharedPreferences
): ViewModel() {
    var state by mutableStateOf(IncidentReportListState())
        private set

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        getIncidentReportPagination()
    }

    fun onEvent(event: IncidentReportListEvent) {
        when(event) {
            is IncidentReportListEvent.AddressChanged -> {
                state = state.copy(address = event.address)
            }
            IncidentReportListEvent.GetIncidentReportList -> {
                getIncidentReportPagination()
            }
            is IncidentReportListEvent.IsSelectedIncidentReportKindChanged -> {
                state = state.copy(selectedIncidentReportKind = event.incidentReportKind)
                getIncidentReportPagination()
            }
            is IncidentReportListEvent.IsSelectedIncidentReportStatusChanged -> {
                state = state.copy(selectedIncidentReportStatus = event.incidentReportStatus)
                getIncidentReportPagination()
            }
        }
    }

    private fun getIncidentReportPagination() {
        state = state.copy(incidentReports = Pager(PagingConfig(pageSize = 5)) {
            IncidentReportListPagination(incidentReportRepository,
                prefs,
                state.selectedIncidentReportKind,
                state.selectedIncidentReportStatus)
        }.flow)
    }

    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }
}