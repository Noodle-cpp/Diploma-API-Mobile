package com.example.greensignal.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class TokenDto (
    @SerializedName("token")
    val token: String
)