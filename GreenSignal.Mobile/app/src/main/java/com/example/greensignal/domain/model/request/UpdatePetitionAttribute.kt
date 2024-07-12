package com.example.greensignal.domain.model.request

data class UpdatePetitionAttribute(
    val name: String = "",
    val stringValue: String? = null,
    val numberValue: Double? = null,
    val boolValue: Boolean? = null,
)
