package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.IncidentAttachmentDto
import com.example.greensignal.data.remote.dto.response.IncidentDto
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatisticDto
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import java.time.LocalDateTime
import java.util.Date

data class Incident(
    val id: String = "",
    val description: String = "",
    val address: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val reportedById: String = "",
    val createdAt: Date = Date(),
    val status: IncidentStatus = IncidentStatus.Submitted,
    val kind: IncidentKind = IncidentKind.Dump,
    val bindingDate: Date? = null,
    val inspectorId: String? = null,
    val incidentAttachments: MutableList<IncidentAttachment> = mutableListOf(),
    val reportedBy: Citizen = Citizen()

)

fun IncidentDto.toIncident(): Incident {
    return  Incident(
        id = id,
        description = description,
        address = address,
        lat = lat,
        lng = lng,
        reportedById = reportedById,
        createdAt = createdAt,
        status = status,
        kind = kind,
        bindingDate = bindingDate,
        inspectorId = inspectorId,
        incidentAttachments = incidentAttachments.map { x -> x.toIncidentAttachment() }.toMutableList(),
        reportedBy = reportedBy.toCitizen()
    )
}