package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.greensignal.domain.repository.PetitionRepository
import com.example.greensignal.presentation.event.PetitionListEvent
import com.example.greensignal.presentation.state.PetitionListState
import com.example.greensignal.presentation.use_case.pagination.PetitionListPagination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class PetitionListViewModel @Inject constructor(
    private val petitionRepository: PetitionRepository,
    private val prefs: SharedPreferences
): ViewModel() {
    var state by mutableStateOf(PetitionListState())
        private set

    private val validationEventChannel = Channel<PetitionListViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        getPetitionPagination()
    }

    fun onEvent(event: PetitionListEvent) {
        when(event) {
            PetitionListEvent.GetPetitionList -> {
                getPetitionPagination()
            }
        }
    }

    private fun getPetitionPagination() {
        state = state.copy(petitions = Pager(PagingConfig(pageSize = 5)) {
            PetitionListPagination(petitionRepository, prefs)
        }.flow)
    }

    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }
}