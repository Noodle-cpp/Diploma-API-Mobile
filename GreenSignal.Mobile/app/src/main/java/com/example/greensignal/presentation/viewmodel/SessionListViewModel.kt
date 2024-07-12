package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.example.greensignal.domain.model.response.toScore
import com.example.greensignal.domain.model.response.toSession
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.presentation.event.RatingEvent
import com.example.greensignal.presentation.event.SessionListEvent
import com.example.greensignal.presentation.state.SessionListState
import com.example.greensignal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionListViewModel @Inject constructor(
    private val inspectorRepository: InspectorRepository,
    private val prefs: SharedPreferences,
): ViewModel() {
    var state by mutableStateOf(SessionListState())
        private set

    private val validationEventChannel = Channel<SessionListViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        onGetSessionList()
    }

    fun onEvent(event: SessionListEvent) {
        when (event) {
            is SessionListEvent.RemoveSession -> {
                onRemoveSession(event.id)
            }
        }
    }

    private fun onRemoveSession(id: String) {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            val token = prefs.getString("inspector-jwt-token", null)

            if(token != null) {
                var jwt: JWT = JWT(token)
                var inspectorId: String? = jwt.getClaim("id").asString()

                when (val response =
                    inspectorRepository.deleteSessions(id, inspectorId!!, token)) {
                    is Resource.Success -> {

                        state = state.copy(
                            isLoading = false,
                            error = null,
                        )

                        validationEventChannel.send(SessionListViewModel.ValidationEvent.Success)
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = response.message!!,
                        )
                    }
                }
            } else {
                state = state.copy(
                    isLoading = false,
                )
            }
        }
        onGetSessionList()
    }

    private fun onGetSessionList() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            val token = prefs.getString("inspector-jwt-token", null)

            if(token != null) {
                var jwt: JWT = JWT(token)
                var inspectorId: String? = jwt.getClaim("id").asString()

                when (val response =
                    inspectorRepository.getSessions(inspectorId!!, token)) {
                    is Resource.Success -> {

                        state = state.copy(
                            isLoading = false,
                            error = null,
                        )

                        state = state.copy(sessions = response.data!!.map { x -> x.toSession() }.toMutableList())

                        validationEventChannel.send(SessionListViewModel.ValidationEvent.Success)
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = response.message!!,
                        )
                    }
                }
            } else {
                state = state.copy(
                    isLoading = false,
                )
            }
        }
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}