package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.LocationDto

data class Location(
    val id: String = "",
    val name: String = "",
    val parentLocationId: String? = null
)

fun LocationDto.toLocation(): Location {
    return  Location(
        id = id,
        name = name,
        parentLocationId = parentLocationId
    )
}