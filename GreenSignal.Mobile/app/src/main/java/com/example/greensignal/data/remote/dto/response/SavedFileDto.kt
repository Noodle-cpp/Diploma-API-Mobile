package com.example.greensignal.data.remote.dto.response

import java.time.LocalDateTime
import java.util.Date

data class SavedFileDto(
    val id: String = "",
    val origName: String = "",
    val path: String = "",
    val createdAt: Date = Date(),
)
