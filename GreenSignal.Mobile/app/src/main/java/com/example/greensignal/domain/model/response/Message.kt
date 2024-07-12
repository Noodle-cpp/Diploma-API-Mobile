package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.MessageAttachmentDto
import com.example.greensignal.data.remote.dto.response.MessageDto
import java.util.Date

data class Message(
    val id: String = "",
    val fromName: String = "",
    val fromAddress: String = "",
    val subject: String = "",
    val content: String = "",
    val createdAt: Date = Date(),
    val seen: Boolean = false,
    val petitionId: String? = null,
    val messageAttachments: MutableList<MessageAttachment> = mutableListOf()
)

fun MessageDto.toMessage() : Message {
    return Message(
        id = id,
        fromName = fromName,
        fromAddress = fromAddress,
        subject = subject,
        content = content,
        createdAt = createdAt,
        seen = seen,
        petitionId = petitionId,
        messageAttachments = messageAttachments.map { x -> x.toMessageAttachment() }.toMutableList()
    )
}
