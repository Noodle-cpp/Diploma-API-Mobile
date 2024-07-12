package com.example.greensignal.presentation.use_case.validation

import javax.inject.Inject

class ValidateDescription @Inject constructor() {
    fun execute(description: String): ValidateResult {
        if(description.isBlank()) {
            return ValidateResult(
                isSuccessful = false,
                errorMessage =  "Описание не должено быть пустым"
            )
        }

        return  ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }
}