package com.example.greensignal.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class AuthorizeCitizenDto(
    @SerializedName("phone")
    val phone: String = "",
    @SerializedName("code")
    val code: String = "",
    @SerializedName("fio")
    val fio: String = ""
)