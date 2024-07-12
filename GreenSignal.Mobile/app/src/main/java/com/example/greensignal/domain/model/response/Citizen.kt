package com.example.greensignal.domain.model.response

import androidx.core.util.rangeTo
import com.example.greensignal.data.remote.dto.response.CitizenDto

data class Citizen(
    val id: String = "",
    val fio: String = "",
    val phone: String = "",
    val rating: Double = 10.0
)

fun  CitizenDto.toCitizen(): Citizen {
    return  Citizen(
        id = id,
        fio = fio,
        phone = phone,
        rating = rating
    )
}
