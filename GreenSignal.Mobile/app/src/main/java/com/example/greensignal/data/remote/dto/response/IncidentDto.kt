package com.example.greensignal.data.remote.dto.response

import java.time.LocalDateTime
import java.util.Date

data class IncidentDto(
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
    val inspectorId: String = "",
    val incidentAttachments: MutableList<IncidentAttachmentDto> = mutableListOf(),
    val reportedBy: CitizenDto = CitizenDto()
)

enum class IncidentKind(val index: Int, val title: String) {
    AirPollution(0, "Загрязнение воздуха"),
    SoilPollution(1, "Загрязнение почвы"),
    Excavation(2, "Добыча недр"),
    Dump(3, "Свалка"),
    TreeCutting(4, "Вырубка лесов"),
    Radiation(5, "Радиация");

    companion object {
        fun getByIndex(index: Int): IncidentKind? {
            return values().find { it.index == index }
        }
    }
}

enum class IncidentStatus(val index: Int, val title: String) {
    Draft(0, "Черновик"),
    Submitted(1, "Ожидает инспектора"),
    Attached(2, "В работе"),
    Completed(3, "Закрыто успешно"),
    Closed(4, "Закрыто безуспешно"),
    Deleted(5, "Удалён");

    companion object {
        fun getByIndex(index: Int): IncidentStatus? {
            return values().find { it.index == index }
        }
    }
}

enum class ReportType(val index: Int, val title: String) {
    Unsolvable(0, "Нерешаемая жалоба"),
    Unacceptable(1, "Неприемлемая жалоба");

    companion object {
        fun getByIndex(index: Int): ReportType? {
            return values().find { it.index == index }
        }
    }
}