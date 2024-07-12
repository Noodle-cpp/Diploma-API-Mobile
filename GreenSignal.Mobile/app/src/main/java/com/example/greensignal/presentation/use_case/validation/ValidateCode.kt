package com.example.greensignal.presentation.use_case.validation

import androidx.core.text.isDigitsOnly
import javax.inject.Inject

class ValidateCode @Inject constructor() {

    fun execute(code: String): ValidateResult {
        if(code.isBlank()) {
            return ValidateResult(
                isSuccessful = false,
                errorMessage = "Код не должен быть пустым"//R.string.code_empty_exception
            )
        }
        else if(code.length != 4) {
            return  ValidateResult(
                isSuccessful = false,
                errorMessage = "Код должен состоять из 4-х символов"//R.string.code_lenght_error
            )
        }
        else if(!code.isDigitsOnly()) {
            return ValidateResult(
                isSuccessful = false,
                errorMessage = "Код должен состоять только из цифр"//R.string.code_symbol_exception
            )
        }

        return ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }
}