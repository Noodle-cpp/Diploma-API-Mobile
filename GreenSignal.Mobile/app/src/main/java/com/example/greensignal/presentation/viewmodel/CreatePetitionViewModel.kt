package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.greensignal.common.Constants
import com.example.greensignal.data.remote.dto.request.toUpdatePetitionAttributeDto
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.PetitionKind
import com.example.greensignal.domain.model.request.CreateIncidentReportAttachment
import com.example.greensignal.domain.model.request.CreatePetition
import com.example.greensignal.domain.model.request.UpdateIncidentReportAttribute
import com.example.greensignal.domain.model.request.UpdatePetitionAttribute
import com.example.greensignal.domain.model.request.toCreatePetitionDto
import com.example.greensignal.domain.model.response.toDepartment
import com.example.greensignal.domain.model.response.toIncident
import com.example.greensignal.domain.repository.DepartmentRepository
import com.example.greensignal.domain.repository.IncidentReportRepository
import com.example.greensignal.domain.repository.PetitionRepository
import com.example.greensignal.presentation.event.CreatePetitionEvent
import com.example.greensignal.presentation.state.CreatePetitionState
import com.example.greensignal.presentation.use_case.pagination.DepartmentListPagination
import com.example.greensignal.presentation.use_case.pagination.IncidentListPagination
import com.example.greensignal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CreatePetitionViewModel @Inject constructor(
    private val prefs: SharedPreferences,
    private val petitionRepository: PetitionRepository,
    private val departmentRepository: DepartmentRepository,
    private val incidentReportRepository: IncidentReportRepository,
) : ViewModel() {
    var state by mutableStateOf(CreatePetitionState())
        private set


    private val validationEventChannel = Channel<CreatePetitionViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: CreatePetitionEvent) {
        when (event) {
            CreatePetitionEvent.CreatePetition -> {
                onCreatePetition()
            }
            is CreatePetitionEvent.DescriptionChanged -> {
                state = state.copy(description = event.description)
            }
            is CreatePetitionEvent.GetDepartment -> {
                if(event.department != null) {
                    onGetDepartment(event.department)
                }
                else {
                    state = state.copy(
                        departmentId = null,
                        selectedDepartment = null,
                    )
                }
            }
            CreatePetitionEvent.GetDepartmentList -> {
                getDepartmentPagination()
            }
            is CreatePetitionEvent.IsBringToJusticeEventsChanged -> {
                state = state.copy(isBringToJustice = event.isBringToJustice)
            }
            is CreatePetitionEvent.IsExaminationEventsChanged -> {
                state = state.copy(isExamination = event.isExamination)
            }
            is CreatePetitionEvent.IsMaterialsEventsChanged -> {
                state = state.copy(isMaterials = event.isMaterials)
            }
            is CreatePetitionEvent.IsOffsiteEventsChanged -> {
                state = state.copy(isOffsiteEvents = event.isOffsite)
            }
            is CreatePetitionEvent.RequirementsChanged -> {
                state = state.copy(requirements = event.requirements)
            }

            is CreatePetitionEvent.IncidentReportChanged -> {
                state = state.copy(incidentReportId = event.incidentReportId)
            }
            is CreatePetitionEvent.PetitionChanged -> {
                state = state.copy(parentPetitionId = event.petitionId)
            }
        }
    }

    private fun getDepartmentPagination() {
        state = state.copy(departments = Pager(PagingConfig(pageSize = 5)) {
            DepartmentListPagination(departmentRepository,
                                        prefs)
        }.flow)
    }

    private fun onGetDepartment(id: String) {
        val token = prefs.getString("inspector-jwt-token", null)

        if (token != null) {
            viewModelScope.launch {
                when (val response = departmentRepository.getDepartment(id, token)) {
                    is Resource.Success -> {
                        if (response.data != null) {
                            state = state.copy(
                               departmentId = response.data.id,
                               selectedDepartment = response.data.toDepartment()
                            )
                        }

                        validationEventChannel.send(CreatePetitionViewModel.ValidationEvent.Success)
                    }


                    is Resource.Error -> {
                        state = state.copy(
                            error = response.message!!,
                        )
                    }
                }
            }
        }
    }

    private fun onCreatePetition() {
        val token = prefs.getString("inspector-jwt-token", null)

        if (token != null) {

            state = state.copy(
                isLoading = true,
                error = null,
            )

            viewModelScope.launch {

                if(state.incidentReportId != null) {
                    val incidentReport = incidentReportRepository.getIncidentReport(
                        state.incidentReportId!!,
                        token
                    ).data

                    if(incidentReport == null) {
                        state = state.copy(
                            isLoading = false,
                            error = "К обращению должен быть прикреплён акт или обращение"
                        )

                        return@launch
                    }

                    state = state.copy(
                        kind = PetitionKind.getByIndex(incidentReport.kind.index)!!,
                        date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(incidentReport.manualDate)
                    )

                } else if(state.parentPetitionId != null) {
                    val parentPetition = petitionRepository.getPetition(state.parentPetitionId!!, token).data

                    if(parentPetition == null) {
                        state = state.copy(
                            isLoading = false,
                            error = "К обращению должен быть прикреплён акт или обращение"
                        )

                        return@launch
                    }

                    state = state.copy(
                        kind = PetitionKind.getByIndex(parentPetition.kind.index)!!,
                        date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(parentPetition.date)
                    )
                } else {
                    state = state.copy(
                        isLoading = false,
                        error = "К обращению должен быть прикреплён акт или обращение"
                    )

                    return@launch
                }

                if(state.departmentId == null) {
                    state = state.copy(
                        isLoading = false,
                        error = "Выбор комитета обязателен"
                    )
                    return@launch
                }

                val createPetition = CreatePetition(
                    date = state.date,
                    departmentId = state.departmentId!!,
                    incidentReportId = state.incidentReportId,
                    parentPetitionId = state.parentPetitionId,
                    kind = state.kind.index,
                    attributeVersion = 2,
                )

                when (val response = petitionRepository.createPetition(createPetition.toCreatePetitionDto(), token)) {
                    is Resource.Success -> {
                        if (response.data != null) {

                            val updateAttributesList = mutableListOf<UpdatePetitionAttribute>()

                            updateAttributesList.add(UpdatePetitionAttribute("OFFSITE_EVENTS", null, null, state.isOffsiteEvents))
                            updateAttributesList.add(UpdatePetitionAttribute("MATERIALS", null, null, state.isMaterials))
                            updateAttributesList.add(UpdatePetitionAttribute("BRING_TO_JUSTICE", null, null, state.isBringToJustice))
                            updateAttributesList.add(UpdatePetitionAttribute("EXAMINATION", null, null, state.isExamination))
                            updateAttributesList.add(UpdatePetitionAttribute("DESCRIPTION", state.description, null, null))
                            updateAttributesList.add(UpdatePetitionAttribute("REQUIREMENTS", state.requirements, null, null))

                            petitionRepository.updatePetitionAttribute(response.data.id, updateAttributesList.map { it.toUpdatePetitionAttributeDto() }.toMutableList(), token)

                            petitionRepository.sentPetition(response.data.id, token)

                            state = state.copy(
                                petitionId = response.data.id,
                                isLoading = false,
                                error = null
                            )
                        }
                        validationEventChannel.send(CreatePetitionViewModel.ValidationEvent.Success)
                    }


                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = response.message!!,
                        )
                    }
                }
            }
        }
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}