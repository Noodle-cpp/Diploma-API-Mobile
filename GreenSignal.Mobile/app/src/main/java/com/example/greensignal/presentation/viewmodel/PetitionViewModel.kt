package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.greensignal.data.remote.dto.response.PetitionStatus
import com.example.greensignal.domain.model.request.UpdatePetition
import com.example.greensignal.domain.model.request.toUpdatePetitionDto
import com.example.greensignal.domain.model.response.toIncident
import com.example.greensignal.domain.model.response.toMessage
import com.example.greensignal.domain.model.response.toPetition
import com.example.greensignal.domain.repository.PetitionRepository
import com.example.greensignal.presentation.event.PetitionEvent
import com.example.greensignal.presentation.state.PetitionState
import com.example.greensignal.presentation.use_case.pagination.MessageListPagination
import com.example.greensignal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class PetitionViewModel @Inject constructor(
    private val petitionRepository: PetitionRepository,
    private val prefs: SharedPreferences
): ViewModel() {
    var state by mutableStateOf(PetitionState())
        private set

    private val validationEventChannel = Channel<PetitionViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: PetitionEvent) {
        when(event) {
            is PetitionEvent.GetPetition -> {
                onGetPetition(event.id)
            }

            PetitionEvent.RemovePetition -> {
                onRemovePetition()
            }

            is PetitionEvent.ClosePetition -> {
                onClosePetition(event.status)
                onGetPetition(state.petition.id)
            }
        }
    }

    private fun onClosePetition(status: PetitionStatus) {
        val token = prefs.getString("inspector-jwt-token", null)

        if(token != null)
        {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true,
                    error = null
                )

                when(val response = if(status == PetitionStatus.Success) petitionRepository.closeSuccessPetition(state.petition.id, token)
                                        else petitionRepository.closeFailedPetition(state.petition.id, token)) {
                    is Resource.Success -> {
                        state = state.copy(
                            isLoading = false,
                            error = null,
                        )

                        validationEventChannel.send(PetitionViewModel.ValidationEvent.Success)
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

    private fun onRemovePetition() {
        val token = prefs.getString("inspector-jwt-token", null)

        if(token != null)
        {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true,
                    error = null
                )

                when(val response = petitionRepository.removePetition(state.petition.id, token)) {
                    is Resource.Success -> {
                        state = state.copy(
                            isLoading = false,
                            error = null,
                        )

                        validationEventChannel.send(PetitionViewModel.ValidationEvent.Success)
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

    private fun onGetPetition(id: String) {
        val token = prefs.getString("inspector-jwt-token", null)

        if(token != null)
        {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true,
                    error = null
                )

                when(val response = petitionRepository.getPetition(id, token)) {
                    is Resource.Success -> {
                        state = state.copy(
                            isLoading = false,
                            error = null,
                            petition = response.data!!.toPetition(),
                        )

                        validationEventChannel.send(PetitionViewModel.ValidationEvent.Success)
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