package com.example.greensignal.domain.model.request

import com.example.greensignal.data.remote.dto.request.UpdateInspectorLocationDto

data class UpdateInspectorLocation(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
)
