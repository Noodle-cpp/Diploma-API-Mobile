package com.example.greensignal.presentation.use_case.validation

import android.net.Uri
import javax.inject.Inject

class ValidateCertificateUri @Inject constructor() {
    fun execute(certificateUri: Uri): ValidateResult {
        if(certificateUri == Uri.EMPTY) {
            return ValidateResult(
                isSuccessful = false,
                errorMessage =  "Сертификат должен быть прикреплён"
            )
        }

        return  ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }
    fun execute(certificateUri: String?): ValidateResult {
        if(certificateUri == null) {
            return ValidateResult(
                isSuccessful = false,
                errorMessage =  "Сертификат должен быть прикреплён"
            )
        }

        return  ValidateResult(
            isSuccessful = true,
            errorMessage = null
        )
    }
}