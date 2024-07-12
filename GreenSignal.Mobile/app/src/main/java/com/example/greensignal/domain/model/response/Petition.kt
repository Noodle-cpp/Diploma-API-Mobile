package com.example.greensignal.domain.model.response

import androidx.compose.runtime.toMutableStateList
import com.example.greensignal.data.remote.dto.response.PetitionDto
import com.example.greensignal.data.remote.dto.response.PetitionKind
import com.example.greensignal.data.remote.dto.response.PetitionStatus
import java.util.Date

data class Petition (
    val id: String = "",
    val serialNumber: String = "",
    val date: Date = Date(),
    val createdAt: Date = Date(),
    val departmentId: String = "",
    val department: Department = Department(),
    val incidentReportId: String? = null,
    val incidentReport: IncidentReport? = null,
    val parentPetitionId: String? = null,
    val parentPetition: Petition? = null,
    val inspectorId: String = "",
    val inspector: Inspector = Inspector(),
    val status: PetitionStatus = PetitionStatus.Draft,
    val kind: PetitionKind = PetitionKind.Dump,
    val attributeVersion: Int = 0,
    val attachements: MutableList<PetitionAttachment> = mutableListOf(),
    val attributes: MutableList<PetitionAttribute> = mutableListOf(),
    val receiveMessages: MutableList<Message> = mutableListOf()
)

fun PetitionDto.toPetition(): Petition {
    return  Petition(
        id = id,
        serialNumber = serialNumber,
        date = date,
        createdAt = createdAt,
        departmentId = departmentId,
        department = department.toDepartment(),
        incidentReportId = incidentReportId,
        incidentReport = incidentReport?.toIncidentReport(),
        parentPetitionId = parentPetitionId,
        parentPetition = parentPetition?.toPetition(),
        inspectorId = inspectorId,
        inspector = inspector.toInspectorAccount(),
        status = status,
        kind = kind,
        attributeVersion = attributeVersion,
        attachements = attachments.map { it.toPetitionAttachment() }.toMutableList(),
        attributes = attributes.map { it.toPetitionAttribute() }.toMutableList(),
        receiveMessages = receiveMessages.map { it.toMessage() }.toMutableList()
    )
}