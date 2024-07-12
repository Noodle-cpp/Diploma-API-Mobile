package com.example.greensignal.presentation.event

import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentStatus

sealed class PetitionListEvent {
    object GetPetitionList: PetitionListEvent()
}