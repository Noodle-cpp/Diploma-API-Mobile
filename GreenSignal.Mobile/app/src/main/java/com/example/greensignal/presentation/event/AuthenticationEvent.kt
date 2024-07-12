package com.example.greensignal.presentation.event

import android.graphics.Bitmap
import java.time.LocalDate
import java.time.LocalDateTime

sealed class AuthenticationEvent {
    data class PhoneChanged(val phone: String): AuthenticationEvent()
    data class FioChanged(val fio: String): AuthenticationEvent()
    data class CodeChanged(val code: String): AuthenticationEvent()
    data class CertificateIdChanged(val certificateId: String): AuthenticationEvent()
    data class CertificateDateChanged(val date: LocalDateTime): AuthenticationEvent()
    data class SchoolIdChanged(val schoolId: String): AuthenticationEvent()

    data class TabChanged(val index: Int): AuthenticationEvent()

    object Authentication: AuthenticationEvent()
    object GetCode: AuthenticationEvent()
    data class CreateInspector(val bitmap: Bitmap): AuthenticationEvent()
}