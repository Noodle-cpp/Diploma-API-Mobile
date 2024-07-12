package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.IncidentReportDto
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus
import java.util.Date

data class IncidentReport(
    val id: String = "",
    val serialNumber: String = "",
    val address: String = "",
    val inspectorId: String = "",
    val inspector: Inspector = Inspector(),
    val description: String = "",
    val createdAt: Date = Date(),
    val manualDate: Date = Date(),
    val startOfInspection: Date = Date(),
    val endOfInspection: Date = Date(),
    val incidentId: String? = null,
    val incident: Incident? = null,
    val locationId: String? = null,
    val location: Location? = null,
    val status: IncidentReportStatus = IncidentReportStatus.Draft,
    val kind: IncidentReportKind = IncidentReportKind.Dump,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val attributesVersion: Int = 0,
    val incidentReportAttachements: MutableList<IncidentReportAttachment> = mutableListOf(),
    val incidentReportAttributes: MutableList<IncidentReportAttribute> = mutableListOf()
)

fun IncidentReportDto.toIncidentReport(): IncidentReport {
    return IncidentReport(
        id = id,
        serialNumber = serialNumber,
        address = address,
        description = description,
        inspectorId = inspectorId,
        inspector = inspector.toInspectorAccount(),
        createdAt = createdAt,
        manualDate = manualDate,
        startOfInspection = startOfInspection,
        endOfInspection = endOfInspection,
        incidentId = incidentId,
        incident = incident?.toIncident(),
        locationId = locationId,
        location = location?.toLocation(),
        status = status,
        kind = kind,
        lat = lat,
        lng = lng,
        attributesVersion = attributesVersion,
        incidentReportAttachements = incidentReportAttachments.map { it.toIncidentReportAttachment() }.toMutableList(),
        incidentReportAttributes = incidentReportAttributes.map { it.toIncidentReportAttribute() }.toMutableList()
    )
}