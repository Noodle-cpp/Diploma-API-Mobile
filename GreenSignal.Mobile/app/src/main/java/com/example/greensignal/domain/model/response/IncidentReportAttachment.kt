package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.IncidentReportAttachmentDto
import com.example.greensignal.data.remote.dto.response.SavedFileDto
import java.util.Date

data class IncidentReportAttachment(
    val id: String = "",
    val savedFileId: String = "",
    val savedFile: SavedFile = SavedFile(),
    val description: String = "",
    val manualDate: Date = Date(),
    val createdAt: Date = Date()
)

fun IncidentReportAttachmentDto.toIncidentReportAttachment(): IncidentReportAttachment {
    return  IncidentReportAttachment(
        id = id,
        savedFile = savedFile.toSavedFile(),
        savedFileId = savedFileId,
        description = description,
        manualDate = manualDate,
        createdAt = createdAt
    )
}
