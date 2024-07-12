package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.auth0.android.jwt.JWT
import com.example.greensignal.common.Constants
import com.example.greensignal.domain.model.response.toInspectorAccount
import com.example.greensignal.domain.model.response.toMessage
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.presentation.event.IncidentListEvent
import com.example.greensignal.presentation.event.MessageListEvent
import com.example.greensignal.presentation.state.IncidentListState
import com.example.greensignal.presentation.state.MessageListState
import com.example.greensignal.presentation.use_case.pagination.IncidentListPagination
import com.example.greensignal.presentation.use_case.pagination.MessageListPagination
import com.example.greensignal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private val inspectorRepository: InspectorRepository,
    private val prefs: SharedPreferences
): ViewModel() {
    var state by mutableStateOf(MessageListState())
        private set

    private val validationEventChannel = Channel<MessageListViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        getMessagesPagination()
    }
    fun onEvent(event: MessageListEvent) {
        when(event) {
            is MessageListEvent.FilterChanged -> {
                state = state.copy(filter = event.filter)
            }
            MessageListEvent.GetMessages -> {
                getMessagesPagination()
            }
        }
    }

    private fun getMessagesPagination() {
        state = state.copy(messages = Pager(PagingConfig(pageSize = 10)) {
            MessageListPagination(inspectorRepository, prefs, state.filter.ifEmpty { null })
        }.flow)
    }

    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }
}