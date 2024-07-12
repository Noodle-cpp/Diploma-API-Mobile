package com.example.greensignal.data.remote.dto.response

import java.util.Date

data class IncidentReportDto(
    val id: String = "",
    val serialNumber: String = "",
    val inspectorId: String = "",
    val inspector: InspectorDto = InspectorDto(),
    val description: String = "",
    val address: String = "",
    val createdAt: Date = Date(),
    val manualDate: Date = Date(),
    val startOfInspection: Date = Date(),
    val endOfInspection: Date = Date(),
    val incidentId: String? = null,
    val incident: IncidentDto? = null,
    val locationId: String? = null,
    val location: LocationDto? = null,
    val status: IncidentReportStatus = IncidentReportStatus.Draft,
    val kind: IncidentReportKind = IncidentReportKind.Dump,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val attributesVersion: Int = 1,
    val incidentReportAttachments: MutableList<IncidentReportAttachmentDto> = mutableListOf(),
    val incidentReportAttributes: MutableList<IncidentReportAttributeDto> = mutableListOf()
)

enum class IncidentReportKind(val index: Int, val title: String) {
    AirPollution(0, "Загрязнение воздуха"),
    SoilPollution(1, "Загрязнение почвы"),
    Excavation(2, "Добыча недр"),
    Dump(3, "Свалка"),
    TreeCutting(4, "Вырубка лесов"),
    Radiation(5, "Радиация");

    companion object {
        fun getByIndex(index: Int): IncidentReportKind? {
            return values().find { it.index == index }
        }
    }
}

enum class IncidentReportStatus(val index: Int, val title: String) {
    Draft(0, "Черновик"),
    Sent(100, "Отправлено"),
    Completed_successfuly(200, "Закрыто успешно"),
    Completed_unsucessful(300, "Закрыто безуспешно"),
    Archived(400, "Удалено");

    companion object {
        fun getByIndex(index: Int): IncidentReportStatus? {
            return values().find { it.index == index }
        }
    }
}
