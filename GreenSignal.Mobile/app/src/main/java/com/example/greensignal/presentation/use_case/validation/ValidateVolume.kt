package com.example.greensignal.presentation.use_case.validation

import javax.inject.Inject

class ValidateVolume @Inject constructor() {
    fun execute(volume: Double, isRequired: Boolean): ValidateResult {
        if(!isRequired)
            return ValidateResult(
                isSuccessful = true,
                errorMessage = null
            )
        else if(volume <= 0) {
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