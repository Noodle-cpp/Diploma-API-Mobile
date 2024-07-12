package com.example.greensignal.data.remote.dto.response

import java.util.Date

data class InspectorDto (
    val id: String = "",
    val fio: String = "",
    val phone: String = "",
    val reviewStatus: InspectorStatus = InspectorStatus.Created,
    val createdAt: Date = Date(),
    val updatedAt: Date? = null,
    val number: Int = -1,
    val internalEmail: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val lastLatLngAt: Date? = null,
    val certificateId: String = "",
    val schoolId: String = "",
    val certificateDate: Date? = null,
    val certificateFile: SavedFileDto? = null,
    val photoFile: SavedFileDto? = null,
    val signature: SavedFileDto? = null,
)


enum class InspectorStatus(val index: Int, val value: String) {
    Created(0, "Создан"),
    VerificationFailed(50, "Отклонён"),
    Active(100, "Подтверждён"),
    Banned(200, "Забанен"),
}