package com.example.greensignal.data.remote.dto.response

import java.util.Date

data class SessionDto(
    val id: String = "",
    val deviceName: String = "",
    val ip: String = "",
    val cretedAt: Date = Date(),
)
