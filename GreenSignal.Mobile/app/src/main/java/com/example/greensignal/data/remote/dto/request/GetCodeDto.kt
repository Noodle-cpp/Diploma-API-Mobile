package com.example.greensignal.data.remote.dto.request

import com.example.greensignal.domain.model.request.GetCode
import com.google.gson.annotations.SerializedName

data class GetCodeDto(
    @SerializedName("phone")
    val phone: String
)

fun GetCode.toGetCodeDto(): GetCodeDto {
    return GetCodeDto(
        phone = phone
    )
}