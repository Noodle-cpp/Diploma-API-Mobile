package com.example.greensignal.domain.repository

import com.example.greensignal.data.remote.dto.response.DepartmentDto
import com.example.greensignal.data.remote.dto.response.IncidentStatisticDto
import com.example.greensignal.util.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped

@ActivityRetainedScoped
interface DepartmentRepository {
    suspend fun getDepartments(page: Int?, perPage: Int?, token: String): Resource<MutableList<DepartmentDto>>
    suspend fun getDepartment(id: String, token: String): Resource<DepartmentDto>
}