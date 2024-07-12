package com.example.greensignal.presentation.state

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class AuthenticationState (
    val tabOptions: List<String> = listOf("Вход", "Регистрация"),
    val selectedTabIndex: Int = 0,

    //LOGIN

    val loginPhone: String = "+7 ",
    val loginPhoneError: String? = null,

    val loginCode: String = "",
    val loginCodeError:String? = null,

    //REGISTRATION

    val registrationPhone: String = "+7 ",
    val registrationPhoneError: String? = null,

    val registrationCode: String = "",
    val registrationCodeError:String? = null,

    val registrationFIO: String = "",
    val registrationFIOError: String? = null,

    val registrationCertificateId: String = "",
    val registrationCertificateIdError: String? = null,

    //val registrationCertificateDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
    val registrationCertificateDate: LocalDateTime = LocalDateTime.now(),
    val registrationCertificateDateError: String? = null,

    val registrationSchoolId: String = "",
    val registrationSchoolIdError: String? = null,

    var registrationCertificateUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY),
    var registrationCertificateUriError: String? = null,

    var registrationPhotoUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY),
    var registrationPhotoUriError: String? = null,

    val isAuthorize: Boolean = false,

    var error: String? = null,
    var isLoading: Boolean = false,
    var isCodeLoading: Boolean = false,
    val isGetCodeSuccess: Boolean = false,
)