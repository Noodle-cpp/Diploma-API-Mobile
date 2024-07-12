package com.example.greensignal.presentation.use_case.validation

import javax.inject.Inject

class ValidateCountOfStumps @Inject constructor() {
    fun execute(countOfStumps: Int, isRequired: Boolean): ValidateResult {
        if(!isRequired)
            return ValidateResult(
                isSuccessful = true,
                errorMessage = null
            )
        else if(countOfStumps <= 0) {
            return  ValidateResult(
                isSuccessful = false,
                errorMessage = "Поле не должно быть отрицательным или равным 0"
            )
        }
        else
            return ValidateResult(
                isSuccessful = true,
                errorMessage = null
            )
    }
}