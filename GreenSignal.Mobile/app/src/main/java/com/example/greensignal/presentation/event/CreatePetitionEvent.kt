package com.example.greensignal.presentation.event

import com.example.greensignal.domain.model.response.Department

sealed class CreatePetitionEvent {
    object CreatePetition: CreatePetitionEvent()
    object GetDepartmentList: CreatePetitionEvent()
    data class GetDepartment(val department: String?): CreatePetitionEvent()
    data class DescriptionChanged(val description: String): CreatePetitionEvent()
    data class IncidentReportChanged(val incidentReportId: String?): CreatePetitionEvent()
    data class PetitionChanged(val petitionId: String?): CreatePetitionEvent()
    data class RequirementsChanged(val requirements: String): CreatePetitionEvent()
    data class IsOffsiteEventsChanged(val isOffsite: Boolean): CreatePetitionEvent()
    data class IsMaterialsEventsChanged(val isMaterials: Boolean): CreatePetitionEvent()
    data class IsBringToJusticeEventsChanged(val isBringToJustice: Boolean): CreatePetitionEvent()
    data class IsExaminationEventsChanged(val isExamination: Boolean): CreatePetitionEvent()
}