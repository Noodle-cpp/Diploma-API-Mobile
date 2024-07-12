package com.example.greensignal.presentation.use_case.pagination

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.auth0.android.jwt.JWT
import com.example.greensignal.domain.model.response.Incident
import com.example.greensignal.domain.model.response.Message
import com.example.greensignal.domain.model.response.toIncident
import com.example.greensignal.domain.model.response.toMessage
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.presentation.viewmodel.HomeViewModel
import com.example.greensignal.util.Resource
import javax.inject.Inject

class MessageListPagination@Inject constructor(
    private val inspectorRepository: InspectorRepository,
    private val prefs: SharedPreferences,
    private val filter: String?
) : PagingSource<Int, Message>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val pageNumber = params.key ?: 1

        val token = prefs.getString("inspector-jwt-token", null)
        if (token != null) {

            val inspectorId: String? = JWT(token).getClaim("id").asString()

            when (val messages = inspectorRepository.getInspectorMessages(inspectorId!!, filter, token)) {
                is Resource.Success -> {
                    return LoadResult.Page(
                        data = messages.data!!.map { x -> x.toMessage() }.toMutableList(),
                        prevKey = null,//if (pageNumber == 1) null else pageNumber - 1,
                        nextKey = null//if (messages.data.isNotEmpty()) pageNumber + 1 else null,
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

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}