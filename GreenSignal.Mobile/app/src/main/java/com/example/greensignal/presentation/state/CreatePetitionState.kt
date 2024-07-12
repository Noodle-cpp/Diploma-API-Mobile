package com.example.greensignal.presentation.state

import androidx.paging.PagingData
import com.example.greensignal.data.remote.dto.response.PetitionKind
import com.example.greensignal.domain.model.response.Department
import com.example.greensignal.domain.model.response.Incident
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class CreatePetitionState (
    val selectedDepartment: Department? = null,
    val description: String = "",
    val incidentReportId: String? = null,
    val parentPetitionId: String? = null,
    val kind: PetitionKind = PetitionKind.Dump,
    val date: String = "",
    val departmentId: String? = null,
    val requirements: String = "",
    val isOffsiteEvents: Boolean = false,
    val isMaterials: Boolean = false,
    val isBringToJustice: Boolean = false,
    val isExamination: Boolean = false,

    val departments: Flow<PagingData<Department>> = emptyFlow(),
    val petitionId: String? = null,

    val isLoading: Boolean = false,
    var error: String? = null,
)