package com.example.greensignal.presentation.event

import android.graphics.Bitmap
import android.net.Uri
import java.time.LocalDateTime

sealed class UpdateInspectorEvent {
    data class PhoneChanged(val phone: String): UpdateInspectorEvent()
    data class FioChanged(val fio: String): UpdateInspectorEvent()
    data class CodeChanged(val code: String): UpdateInspectorEvent()
    data class CertificateIdChanged(val certificateId: String): UpdateInspectorEvent()
    data class CertificateDateChanged(val date: LocalDateTime): UpdateInspectorEvent()
    data class SchoolIdChanged(val schoolId: String): UpdateInspectorEvent()
    object GetCode: UpdateInspectorEvent()
    object UpdateInspector: UpdateInspectorEvent()
    data class UpdateInspectorPhoto(val uri: Uri): UpdateInspectorEvent()
    data class UpdateInspectorSignature(val bitmap: Bitmap): UpdateInspectorEvent()
    data class UpdateInspectorCert(val uri: Uri): UpdateInspectorEvent()
}