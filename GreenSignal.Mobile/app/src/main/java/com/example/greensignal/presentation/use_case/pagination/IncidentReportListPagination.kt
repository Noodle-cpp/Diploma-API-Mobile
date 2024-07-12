package com.example.greensignal.presentation.use_case.pagination

import android.content.SharedPreferences
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus
import com.example.greensignal.domain.model.response.IncidentReport
import com.example.greensignal.domain.model.response.toIncidentReport
import com.example.greensignal.domain.repository.IncidentReportRepository
import com.example.greensignal.util.Resource
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class IncidentReportListPagination @Inject constructor(
    private val incidentReportRepository: IncidentReportRepository,
    private val prefs: SharedPreferences,
    private val incidentReportKind: IncidentReportKind?,
    private val incidentReportStatus: IncidentReportStatus?
) : PagingSource<Int, IncidentReport>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IncidentReport> {
        val pageNumber = params.key ?: 1

        val token = prefs.getString("inspector-jwt-token", null)
        if (token != null) {
            when (val incidentsReport =
                incidentReportRepository.getIncidentReportList(
                    pageNumber,
                    5,
                    incidentReportKind,
                    incidentReportStatus,
                    token
                )) {
                is Resource.Success -> {
                    return LoadResult.Page(
                        data = incidentsReport.data!!.map { x -> x.toIncidentReport() }
                            .toMutableList(),
                        prevKey = if (pageNumber == 1) null else pageNumber - 1,
                        nextKey = if (incidentsReport.data.isNotEmpty()) pageNumber + 1 else null,
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

    override fun getRefreshKey(state: PagingState<Int, IncidentReport>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}