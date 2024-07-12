package com.example.greensignal.presentation.state

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.greensignal.data.remote.dto.response.InspectorStatus
import java.time.LocalDateTime

data class UpdateInspectorState (
    val status: InspectorStatus = InspectorStatus.Created,

    val updatePhone: String = "+7 ",
    val updatePhoneError: String? = null,

    val updateCode: String = "",
    val updateCodeError:String? = null,
    val isCodeLoading: Boolean = false,
    val isGetCodeSuccess: Boolean = false,

    val updateFIO: String = "",
    val updateFIOError: String? = null,

    val updateCertificateId: String = "",
    val updateCertificateIdError: String? = null,

    val updateCertificateDate: LocalDateTime = LocalDateTime.now(),
    val updateCertificateDateError: String? = null,

    val updateSchoolId: String = "",
    val updateSchoolIdError: String? = null,

    var updateCertificateUri: String? = null,
    var updateCertificateUriError: String? = null,

    var updatePhotoUri: String? = null,
    var updatePhotoUriError: String? = null,

    var error: String? = null,
    var isLoading: Boolean = false,
    var isSuccess: Boolean = false
)