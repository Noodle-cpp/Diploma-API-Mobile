package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.SavedFileDto
import java.util.Date

data class SavedFile(
    val id: String = "",
    val origName: String = "",
    val path: String = "",
    val createdAt: Date = Date(),
)

fun SavedFileDto.toSavedFile(): SavedFile {
    return  SavedFile(
        id = id,
        origName = origName,
        path = path,
        createdAt = createdAt
    )
}