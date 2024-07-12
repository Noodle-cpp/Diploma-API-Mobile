package com.example.greensignal.presentation.use_case.pagination

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.auth0.android.jwt.JWT
import com.example.greensignal.domain.model.response.Department
import com.example.greensignal.domain.model.response.toDepartment
import com.example.greensignal.domain.repository.DepartmentRepository
import com.example.greensignal.util.Resource
import javax.inject.Inject

class DepartmentListPagination @Inject constructor(
    private val departmentRepository: DepartmentRepository,
    private val prefs: SharedPreferences,
) : PagingSource<Int, Department>()  {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Department> {
        val pageNumber = params.key ?: 1

        val token = prefs.getString("inspector-jwt-token", null)
        if (token != null) {
            var jwt: JWT = JWT(token)
            var inspectorId: String? = jwt.getClaim("id").asString()

            if (inspectorId != null) {
                when (val incidents =

                    departmentRepository.getDepartments(
                        pageNumber,
                        5,
                        token
                    )) {
                    is Resource.Success -> {
                        return LoadResult.Page(
                            data = incidents.data!!.map { x -> x.toDepartment() }.toMutableList(),
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

    override fun getRefreshKey(state: PagingState<Int, Department>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}