package com.example.greensignal.presentation.state

import androidx.paging.PagingData
import com.example.greensignal.domain.model.response.Petition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class PetitionListState (
    val petitions: Flow<PagingData<Petition>> = emptyFlow(),

    val page: Int? = null,
    val perPage: Int? = null,

    val isLoading: Boolean = false,
    val error: String? = null
)