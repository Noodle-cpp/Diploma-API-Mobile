package com.example.greensignal.presentation.use_case.validation

import javax.inject.Inject

class ValidateCertificateId @Inject constructor() {
    fun execute(certificateId: String): ValidateResult {
        if(certificateId.isBlank()) {
            return  ValidateResult(
                isSuccessful = false,
                errorMessage = "Поле не должно быть пустым"
            )
        }
        else if(!patternMatcher(certificateId)) {
            return  ValidateResult(
                isSuccessful = false,
                errorMessage = "Сертификат должен иметь формат: 123-4567"
            )
        }
        return ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }

    private fun patternMatcher(input: String): Boolean {
        val regex = Regex(pattern = "([0-9]{3})-([0-9]{4})", options = setOf(RegexOption.IGNORE_CASE))
        return regex.matches(input)
    }
}
