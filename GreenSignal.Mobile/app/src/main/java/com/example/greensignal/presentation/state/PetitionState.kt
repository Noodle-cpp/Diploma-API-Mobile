package com.example.greensignal.presentation.state

import androidx.paging.PagingData
import com.example.greensignal.domain.model.response.Message
import com.example.greensignal.domain.model.response.Petition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class PetitionState(
    val petition: Petition = Petition(),
    val messages: Flow<PagingData<Message>> = emptyFlow(),

    val isLoading: Boolean = false,
    val error: String? = null
)
