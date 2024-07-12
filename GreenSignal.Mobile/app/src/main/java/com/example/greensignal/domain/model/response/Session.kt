package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.SessionDto
import java.util.Date

data class Session(
    val id: String = "",
    val deviceName: String = "",
    val ip: String = "",
    val cretedAt: Date = Date(),
)

fun SessionDto.toSession(): Session {
    return Session(
        id = id,
        deviceName = deviceName,
        ip = ip,
        cretedAt = cretedAt
    )
}