package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.greensignal.domain.repository.IncidentReportRepository
import com.example.greensignal.domain.repository.IncidentRepository
import com.example.greensignal.presentation.event.HomeEvent
import com.example.greensignal.presentation.state.HomeState
import com.example.greensignal.presentation.use_case.pagination.HomeIncidentListPagination
import com.example.greensignal.presentation.use_case.pagination.IncidentListPagination
import com.example.greensignal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val incidentReportRepository: IncidentReportRepository,
    private val prefs: SharedPreferences
    ) : ViewModel() {
    var state by mutableStateOf(HomeState())
        private set

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        onAuthorize()
        onGetIncidentReportStatistic()
        onGetIncidentStatistic()
        onGetIncidentsForCitizen()
        val token = prefs.getString("citizen-jwt-token", null)
        state = state.copy(isCitizenAuthorized = token != null)
    }

    fun onEvent(event: HomeEvent) {
        when(event) {
            HomeEvent.GetIncidentReportStatistic -> {
                onGetIncidentReportStatistic()
            }

            HomeEvent.GetIncidentStatistic -> {
                onGetIncidentStatistic()
            }

            HomeEvent.GetIncidents -> {
                onGetIncidentsForCitizen()
            }

            HomeEvent.CheckToken -> {
                onAuthorize()
            }
        }
    }

    private fun onAuthorize() {
        state = state.copy(
            isLoading = true,
            error = null,
        )

        val token = prefs.getString("inspector-jwt-token", null)
        state = state.copy(isAuthorized = token != null)

        state = state.copy(
            isLoading = false,
            error = null,
        )
    }

    private fun onGetIncidentStatistic() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            when(val response = incidentRepository.getIncidentStatistic()) {
                is Resource.Success -> {
                    state = state.copy(
                        isLoading = false,
                        error = null,
                        countOfCompletedIncidents = response.data!!.countOfCompletedIncidents,
                        countOfIncidents = response.data.countOfIncidents,
                        countOfIncidentsInWork = response.data.countOfIncidentsInWork
                    )

                    validationEventChannel.send(ValidationEvent.Success)
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = response.message!!
                    )
                }
            }
        }
    }

    private fun onGetIncidentReportStatistic() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            when(val response = incidentReportRepository.getIncidentReportStatistic()) {
                is Resource.Success -> {
                    state = state.copy(
                        isLoading = false,
                        error = null,
                        countOfSentIncidentReports = response.data!!.countOfSentIncidentReports,
                    )

                    validationEventChannel.send(ValidationEvent.Success)
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = response.message!!
                    )
                }
            }
        }
    }

    private fun onGetIncidentsForCitizen() {
        val tokenCitizen = prefs.getString("citizen-jwt-token", null)

        if (tokenCitizen != null) {

            state = state.copy(
                isLoadingPagination = true,
                error = null,
            )

            state = state.copy(incidents = Pager(PagingConfig(pageSize = 2)) {
                HomeIncidentListPagination(
                    incidentRepository,
                    prefs
                )
            }.flow)

            state = state.copy(
                isLoadingPagination = false,
                error = null,
            )
        }
    }
    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }
}