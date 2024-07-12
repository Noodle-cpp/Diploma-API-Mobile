package com.example.greensignal.presentation.use_case.validation

import android.os.PatternMatcher
import javax.inject.Inject

class ValidateSchoolId @Inject constructor() {
    fun execute(schoolId: String): ValidateResult {
        if(schoolId.isBlank()) {
            return  ValidateResult(
                isSuccessful = false,
                errorMessage = "Поле не должно быть пустым"
            )
        }
        else if(!patternMatcher(schoolId)) {
            return  ValidateResult(
                isSuccessful = false,
                errorMessage = "Аттестат должен иметь формат: 123-45678-90"
            )
        }
        return ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }

    private fun patternMatcher(input: String): Boolean {
        val regex = Regex(pattern = "([0-9]{3})-([0-9]{5})-([0-9]{2})", options = setOf(RegexOption.IGNORE_CASE))
        return regex.matches(input)
    }
}
