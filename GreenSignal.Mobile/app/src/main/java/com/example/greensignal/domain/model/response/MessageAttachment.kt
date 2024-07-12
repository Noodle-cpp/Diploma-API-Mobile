package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.MessageAttachmentDto

data class MessageAttachment(
    val savedFile: SavedFile = SavedFile()
)

fun MessageAttachmentDto.toMessageAttachment(): MessageAttachment {
    return  MessageAttachment(
        savedFile = savedFile.toSavedFile()
    )
}
