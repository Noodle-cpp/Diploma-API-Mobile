package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import android.telephony.PhoneNumberUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.example.greensignal.data.remote.dto.response.ReportType
import com.example.greensignal.domain.model.response.toIncident
import com.example.greensignal.domain.repository.IncidentRepository
import com.example.greensignal.presentation.event.AuthenticationEvent
import com.example.greensignal.presentation.event.IncidentEvent
import com.example.greensignal.presentation.event.IncidentListEvent
import com.example.greensignal.presentation.state.IncidentListState
import com.example.greensignal.presentation.state.IncidentState
import com.example.greensignal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncidentViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val prefs: SharedPreferences
): ViewModel() {
    var state by mutableStateOf(IncidentState())
        private set

    private val validationEventChannel = Channel<IncidentViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        val token = prefs.getString("inspector-jwt-token", null)
        if(token != null) {
            val inspectorId: String? = JWT(token).getClaim("id").asString()
            state = state.copy(inspectorId = inspectorId!!)
        }
    }

    fun onEvent(event: IncidentEvent) {
        when(event) {
            is IncidentEvent.GetIncident -> {
                onGetIncident(event.id)
            }

            is IncidentEvent.GetIncidentInWork -> {
                onGetIncidentInWork(event.id)
            }

            is IncidentEvent.ReportIncident -> {
                onReportIncident(event.type)
            }
        }
    }

    private fun onReportIncident(type: ReportType) {
        val token = prefs.getString("inspector-jwt-token", null)

        if(token != null)
        {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true,
                    error = null
                )

                when(val response = incidentRepository.reportIncident(state.incident.id, token, type)) {
                    is Resource.Success -> {
                        state = state.copy(
                            isLoading = false,
                            error = null,
                        )

                        validationEventChannel.send(IncidentViewModel.ValidationEvent.Success)
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
    }

    private fun onGetIncident(id: String) {
        val token = prefs.getString("inspector-jwt-token", null)

        if(token != null)
        {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true,
                    error = null
                )

                when(val response = incidentRepository.getIncident(id, token)) {
                    is Resource.Success -> {
                        state = state.copy(
                            isLoading = false,
                            error = null,
                            incident = response.data!!.toIncident()
                        )

                        validationEventChannel.send(IncidentViewModel.ValidationEvent.Success)
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
    }

    private fun onGetIncidentInWork(id: String) {
        val token = prefs.getString("inspector-jwt-token", null)

        if(token != null)
        {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true,
                    error = null
                )

                when(val response = incidentRepository.getIncidentInWork(id, token)) {
                    is Resource.Success -> {
                        state = state.copy(
                            isLoading = false,
                            error = null,
                            incident = response.data!!.toIncident()
                        )

                        validationEventChannel.send(IncidentViewModel.ValidationEvent.Success)
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
    }

    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }
}