package com.example.greensignal.presentation.event

import com.example.greensignal.data.remote.dto.response.PetitionStatus

sealed class PetitionEvent {
    data class GetPetition(val id: String): PetitionEvent()
    data class ClosePetition(val status: PetitionStatus): PetitionEvent()
    object RemovePetition: PetitionEvent()
}