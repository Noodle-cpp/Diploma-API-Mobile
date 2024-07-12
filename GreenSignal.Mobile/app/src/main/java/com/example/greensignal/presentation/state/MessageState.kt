package com.example.greensignal.presentation.state

import androidx.paging.PagingData
import com.example.greensignal.domain.model.response.Petition
import com.example.greensignal.domain.model.response.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class MessageState(
    val message: Message = Message(),
    val inspectorId: String = "",
    val petition: Petition? = null,

    val petitions: Flow<PagingData<Petition>> = emptyFlow(),

    val isLoading: Boolean = false,
    val error: String? = null
)
