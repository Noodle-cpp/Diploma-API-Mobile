package com.example.greensignal.presentation.use_case.validation

import javax.inject.Inject

class ValidatePhone @Inject constructor() {
    fun execute(phone: String): ValidateResult {
        if(phone.isBlank()) {
            return ValidateResult(
                isSuccessful = false,
                errorMessage =  "Номер телефона не должен быть пустым"//R.string.phone_empty_exception
            )
        }
        else if(phone.length != 16) {
            return  ValidateResult(
                isSuccessful = false,
                errorMessage = "Номер телефона имеет неверный формат"//R.string.phone_incorrect_exception
            )
        }
        else if(phone.substring(startIndex = 0, endIndex = 3) != "+7 ") {
            val a = phone.substring(startIndex = 0, endIndex = 2)
            return  ValidateResult(
                isSuccessful = false,
                errorMessage = "Номер телефона должен иметь формат: +7 123 456-78-90"//R.string.phone_incorrect_exception
            )
        }

        return  ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }
}