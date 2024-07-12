package com.example.greensignal.presentation.use_case.pagination

import android.content.SharedPreferences
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.auth0.android.jwt.JWT
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import com.example.greensignal.domain.model.response.Incident
import com.example.greensignal.domain.model.response.toIncident
import com.example.greensignal.domain.model.response.toMessage
import com.example.greensignal.domain.repository.IncidentRepository
import com.example.greensignal.util.Resource
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class IncidentListPagination @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val prefs: SharedPreferences,
    private val incidentKind: IncidentKind?,
    private val incidentStatus: IncidentStatus?,
    private val tabIndex: Int,
    private val isNerby: Boolean,
) : PagingSource<Int, Incident>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Incident> {
        val pageNumber = params.key ?: 1

        val token = prefs.getString("inspector-jwt-token", null)
        if (token != null) {
            var jwt: JWT = JWT(token)
            var inspectorId: String? = jwt.getClaim("id").asString()

            if (inspectorId != null) {
                when (val incidents =
                    if (tabIndex == 0) incidentRepository.getIncidents(
                        pageNumber,
                        5,
                        isNerby,
                        incidentKind,
                        token
                    )
                    else incidentRepository.getInspectorIncidents(
                        inspectorId,
                        pageNumber,
                        5,
                        incidentKind,
                        incidentStatus,
                        token
                    )) {
                    is Resource.Success -> {
                        return LoadResult.Page(
                            data = incidents.data!!.map { x -> x.toIncident() }.toMutableList(),
                            prevKey = if (pageNumber == 1) null else pageNumber - 1,
                            nextKey = if (incidents.data.isNotEmpty()) pageNumber + 1 else null,
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
            }
            else {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } else {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Incident>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}