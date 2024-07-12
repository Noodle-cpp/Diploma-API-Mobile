package com.example.greensignal.data.remote.dto.response

import java.util.Date

data class MessageDto(
    val id: String = "",
    val fromName: String = "",
    val fromAddress: String = "",
    val subject: String = "",
    val content: String = "",
    val createdAt: Date = Date(),
    val seen: Boolean = false,
    val petitionId: String? = null,
    val messageAttachments: MutableList<MessageAttachmentDto> = mutableListOf()
)
