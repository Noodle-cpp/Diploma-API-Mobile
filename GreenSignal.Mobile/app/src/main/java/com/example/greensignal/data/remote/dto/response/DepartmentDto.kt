package com.example.greensignal.data.remote.dto.response

data class DepartmentDto(
    val id: String = "",
    val name: String = "",
    val aliasNames: String = "",
    val address: String = "",
    val email: String = "",
    val isActive: Boolean = true,
    val locationId: String = "",
    val location: LocationDto = LocationDto()
)