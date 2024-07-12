package com.example.greensignal.data.remote.dto.response

import java.util.Date

data class IncidentReportAttachmentDto(
    val id: String = "",
    val savedFileId: String = "",
    val savedFile: SavedFileDto = SavedFileDto(),
    val description: String = "",
    val manualDate: Date = Date(),
    val createdAt: Date = Date()
)
