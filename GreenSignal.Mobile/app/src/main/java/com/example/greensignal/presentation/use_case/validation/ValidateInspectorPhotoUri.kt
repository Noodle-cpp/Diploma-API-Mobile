package com.example.greensignal.presentation.use_case.validation

import android.net.Uri
import javax.inject.Inject

class ValidateInspectorPhotoUri @Inject constructor() {
    fun execute(photoUri: Uri): ValidateResult {
        if(photoUri == Uri.EMPTY) {
            return ValidateResult(
                isSuccessful = false,
                errorMessage =  "Фотография профиля должна быть прикреплена"
            )
        }

        return  ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }
    fun execute(photoUri: String?): ValidateResult {
        if(photoUri == null) {
            return ValidateResult(
                isSuccessful = false,
                errorMessage =  "Фотография профиля должна быть прикреплена"
            )
        }

        return  ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }
}