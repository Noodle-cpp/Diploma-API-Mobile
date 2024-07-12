package com.example.greensignal.data.remote.dto.request

import com.example.greensignal.domain.model.request.AuthorizeInspector
import com.google.gson.annotations.SerializedName

data class AuthorizeInspectorDto (
    @SerializedName("phone")
    val phone: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("deviceName")
    val deviceName: String,
    @SerializedName("firebaseToken")
    val firebaseToken: String
)

fun AuthorizeInspector.toAuthorizeInspectorDto(): AuthorizeInspectorDto {
    return AuthorizeInspectorDto(
        phone = phone,
        code = code,
        deviceName = deviceName,
        firebaseToken = firebaseToken
    )
}