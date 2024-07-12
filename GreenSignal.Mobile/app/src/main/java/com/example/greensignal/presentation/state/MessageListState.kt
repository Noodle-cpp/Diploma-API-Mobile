package com.example.greensignal.presentation.state

import androidx.paging.PagingData
import com.example.greensignal.domain.model.response.Incident
import com.example.greensignal.domain.model.response.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class MessageListState(
    val messages: Flow<PagingData<Message>> = emptyFlow(),
    val filter: String = "",

    val page: Int? = null,
    val perPage: Int? = null,

    val isLoading: Boolean = false,
    val error: String? = null
)
