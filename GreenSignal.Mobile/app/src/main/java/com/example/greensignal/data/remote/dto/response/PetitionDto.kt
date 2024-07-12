package com.example.greensignal.data.remote.dto.response

import java.util.Date

data class PetitionDto(
    val id: String = "",
    val serialNumber: String = "",
    val date: Date = Date(),
    val createdAt: Date = Date(),
    val departmentId: String,
    val department: DepartmentDto = DepartmentDto(),
    val incidentReportId: String? = null,
    val incidentReport: IncidentReportDto? = null,
    val parentPetitionId: String? = null,
    val parentPetition: PetitionDto? = null,
    val inspectorId: String = "",
    val inspector: InspectorDto = InspectorDto(),
    val status: PetitionStatus = PetitionStatus.Draft,
    val kind: PetitionKind = PetitionKind.Dump,
    val attributeVersion: Int = 0,
    val attachments: MutableList<PetitionAttachmentDto> = mutableListOf(),
    val attributes: MutableList<PetitionAttributeDto> = mutableListOf(),
    val receiveMessages: MutableList<MessageDto> = mutableListOf()
)

enum class PetitionKind(val index: Int, val title: String) {
    AirPollution(0, "Загрязнение воздуха"),
    SoilPollution(1, "Загрязнение почвы"),
    Excavation(2, "Раскопки"),
    Dump(3, "Свалка"),
    TreeCutting(4, "Вырубка деревьев"),
    Radiation(5, "Радиация");

    companion object {
        fun getByIndex(index: Int): PetitionKind? {
            return PetitionKind.values().find { it.index == index }
        }
    }
}

enum class PetitionStatus(val index: Int, val title: String)
{
    Draft(0, "Черновик"),
    Sent(100, "Отправлено"),
    Replied(200, "Получен ответ"),
    Success(300, "Закрыто успешно"),
    Failed(500, "Закрыто безуспешно"),
    Archived(600, "Удалено")
}
