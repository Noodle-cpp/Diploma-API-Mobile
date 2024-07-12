package com.example.greensignal.util

import com.google.gson.annotations.SerializedName

data class JwtPayload(
    @SerializedName("id")
    val id: String
)
