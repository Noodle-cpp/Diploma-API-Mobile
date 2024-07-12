package com.example.greensignal.presentation.use_case.validation

import javax.inject.Inject

class ValidateAddress @Inject constructor() {
    fun execute(address: String): ValidateResult {
        if(address.isBlank()) {
            return  ValidateResult(
                isSuccessful = false,
                errorMessage = "Поле не должно быть пустым"
            )
        }
        else
        return ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }
}