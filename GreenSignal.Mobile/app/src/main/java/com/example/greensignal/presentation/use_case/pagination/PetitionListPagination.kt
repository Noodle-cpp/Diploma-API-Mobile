package com.example.greensignal.presentation.use_case.pagination

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.auth0.android.jwt.JWT
import com.example.greensignal.data.remote.dto.response.PetitionStatus
import com.example.greensignal.domain.model.response.Petition
import com.example.greensignal.domain.model.response.toPetition
import com.example.greensignal.domain.repository.PetitionRepository
import com.example.greensignal.util.Resource
import javax.inject.Inject

class PetitionListPagination @Inject constructor(
    private val petitionRepository: PetitionRepository,
    private val prefs: SharedPreferences,
) : PagingSource<Int, Petition>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Petition> {
        val pageNumber = params.key ?: 1

        val token = prefs.getString("inspector-jwt-token", null)
        if (token != null) {
            when (val petitions =
                petitionRepository.getPetitionList(
                    pageNumber,
                    5,
                    token
                )) {
                is Resource.Success -> {
                    return LoadResult.Page(
                        data = petitions.data!!.map { x -> x.toPetition() }.toMutableList(),
                        prevKey = if (pageNumber == 1) null else pageNumber - 1,
                        nextKey = if (petitions.data.isNotEmpty()) pageNumber + 1 else null,
                    )
                }

                is Resource.Error -> {
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    )
                }
            }
        } else {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Petition>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}