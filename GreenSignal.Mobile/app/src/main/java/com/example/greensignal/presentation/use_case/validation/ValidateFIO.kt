package com.example.greensignal.presentation.use_case.validation

import javax.inject.Inject

class ValidateFIO @Inject constructor() {
    fun execute(fio: String): ValidateResult {
        if(fio.isBlank()) {
            return  ValidateResult(
                isSuccessful = false,
                errorMessage = "Поле Ф.И.О. не должно быть пустым"
            )
        }

        return ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }
}