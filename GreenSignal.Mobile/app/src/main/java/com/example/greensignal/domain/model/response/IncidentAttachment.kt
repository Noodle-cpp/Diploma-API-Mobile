package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.IncidentAttachmentDto
import com.example.greensignal.data.remote.dto.response.SavedFileDto

data class IncidentAttachment(
    val id: String = "",
    val savedFileId: String = "",
    val savedFile: SavedFile = SavedFile(),
    val description: String = ""
)

fun IncidentAttachmentDto.toIncidentAttachment(): IncidentAttachment {
    return IncidentAttachment(
        id = id,
        savedFileId = savedFileId,
        savedFile =  savedFile.toSavedFile(),
        description = description
    )
}