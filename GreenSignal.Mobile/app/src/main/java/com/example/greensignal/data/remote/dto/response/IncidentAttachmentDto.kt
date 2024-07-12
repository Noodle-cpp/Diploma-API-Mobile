package com.example.greensignal.data.remote.dto.response

data class IncidentAttachmentDto(
    val id: String = "",
    val savedFileId: String = "",
    val savedFile: SavedFileDto = SavedFileDto(),
    val description: String = ""
)
