package com.example.greensignal.presentation.use_case.validation

import javax.inject.Inject

class ValidateTypeOfMiniral @Inject constructor() {
    fun execute(typeOfMiniral: String, isRequired: Boolean): ValidateResult {
        if(!isRequired)
            return ValidateResult(
                isSuccessful = true,
                errorMessage = null
            )
        else if(typeOfMiniral.isBlank()) {
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