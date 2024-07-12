package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.PetitionAttachmentDto
import com.example.greensignal.data.remote.dto.response.SavedFileDto
import java.util.Date

data class PetitionAttachment(
    val id: String = "",
    val savedFileId: String = "",
    val savedFile: SavedFileDto = SavedFileDto(),
    val description: String = "",
    val manualDate: Date = Date(),
    val createdAt: Date = Date()
)

fun PetitionAttachmentDto.toPetitionAttachment(): PetitionAttachment {
    return PetitionAttachment(
        id = id,
        savedFileId = savedFileId,
        savedFile = savedFile,
        description = description,
        manualDate = manualDate,
        createdAt = createdAt
    )
}
