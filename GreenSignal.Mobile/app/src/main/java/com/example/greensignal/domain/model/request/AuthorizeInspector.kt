package com.example.greensignal.domain.model.request

import com.example.greensignal.data.remote.dto.request.AuthorizeInspectorDto

data class AuthorizeInspector(
    val phone: String,
    val code: String,
    val deviceName: String,
    val firebaseToken: String
)

