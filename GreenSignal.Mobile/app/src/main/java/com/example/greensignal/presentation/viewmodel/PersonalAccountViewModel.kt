package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.presentation.event.InspectorAccountEvent
import com.example.greensignal.presentation.event.PersonalAccountEvent
import com.example.greensignal.presentation.state.InspectorProfileState
import com.example.greensignal.presentation.state.PersonalAccountState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class PersonalAccountViewModel@Inject constructor(
    private val prefs: SharedPreferences,
) : ViewModel() {
    var state by mutableStateOf(PersonalAccountState())
        private set

    private val validationEventChannel = Channel<PersonalAccountViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: PersonalAccountEvent) {
        when (event) {
            else -> {}
        }
    }

    init {
        val token = prefs.getString("inspector-jwt-token", null)
        if(token == null) {
            state = state.copy(
                isAuthorized = false
            )
        }
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}