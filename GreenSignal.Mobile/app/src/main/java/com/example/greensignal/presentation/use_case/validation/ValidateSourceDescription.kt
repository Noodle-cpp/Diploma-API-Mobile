package com.example.greensignal.presentation.use_case.validation

import javax.inject.Inject

class ValidateSourceDescription @Inject constructor() {
    fun execute(sourceDescription: String, isRequired: Boolean): ValidateResult {
        if(!isRequired)
            return ValidateResult(
                isSuccessful = true,
                errorMessage = null
            )
        else if(sourceDescription.isBlank()) {
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