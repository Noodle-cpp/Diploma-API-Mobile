package com.example.greensignal.presentation.use_case.validation

data class ValidateResult(
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)