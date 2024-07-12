package com.example.greensignal.presentation.state

import com.example.greensignal.data.remote.dto.response.InspectorStatus
import java.time.LocalDateTime
import java.util.Date

data class InspectorProfileState (
    val fio: String = "",
    val phone: String = "",
    val status: InspectorStatus = InspectorStatus.Created,
    val certificateId: String = "",
    val certificateDate: String = "",
    val schoolId: String = "",
    var photoFile: String? = null,
    var certFile: String? = null,
    var signatureFile: String? = null,

    var error: String? = null,
    var isLoading: Boolean = false,
    var isAuthorized: Boolean = true,

    var isPhotoLoaded : Boolean = false,
    var isCertLoaded : Boolean = false,
    var isSignatureLoaded : Boolean = false,
)
