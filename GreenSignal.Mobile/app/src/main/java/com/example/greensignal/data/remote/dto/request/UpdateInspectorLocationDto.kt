package com.example.greensignal.data.remote.dto.request

import com.example.greensignal.domain.model.request.UpdateInspectorLocation

data class UpdateInspectorLocationDto(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
)


fun UpdateInspectorLocation.toUpdateInspectorLocationDto(): UpdateInspectorLocationDto {
    return UpdateInspectorLocationDto(
        lat = lat,
        lng = lng
    )
}