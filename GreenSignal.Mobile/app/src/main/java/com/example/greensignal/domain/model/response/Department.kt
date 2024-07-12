package com.example.greensignal.domain.model.response

import com.example.greensignal.data.remote.dto.response.DepartmentDto

data class Department(
    val id: String = "",
    val name: String = "",
    val aliasNames: String = "",
    val address: String = "",
    val email: String = "",
    val isActive: Boolean = true,
    val locationId: String = "",
    val location: Location = Location()
)

fun DepartmentDto.toDepartment(): Department {
    return Department(
        id = id,
        name = name,
        aliasNames = aliasNames,
        address = address,
        email = email,
        isActive = isActive,
        locationId = locationId,
        location = location.toLocation()
    )
}
