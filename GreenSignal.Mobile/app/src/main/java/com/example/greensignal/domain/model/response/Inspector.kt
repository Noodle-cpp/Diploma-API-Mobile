package com.example.greensignal.domain.model.response

import android.telephony.PhoneNumberUtils
import com.example.greensignal.data.remote.dto.response.InspectorDto
import com.example.greensignal.data.remote.dto.response.InspectorStatus
import com.example.greensignal.data.remote.dto.response.SavedFileDto
import java.util.Date

data class Inspector(
    val id: String = "",
    val fio: String = "",
    val phone: String = "",
    val reviewStatus: InspectorStatus = InspectorStatus.Created,
    val createdAt: Date = Date(),
    val updatedAt: Date? = null,
    val number: Int = -1,
    val internalEmail: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val lastLatLngAt: Date? = null,
    val certificateId: String = "",
    val schoolId: String = "",
    val certificateDate: Date? = null,
    val certificateFile: SavedFileDto? = null,
    val photoFile: SavedFileDto? = null,
    val signature: SavedFileDto? = null,
)

fun InspectorDto.toInspectorAccount(): Inspector {
    return Inspector(
        id = id,
        fio = fio,
        phone = formatPhoneNumber(phone),
        reviewStatus = reviewStatus,
        createdAt = createdAt,
        updatedAt = updatedAt,
        number = number,
        internalEmail = internalEmail,
        lat = lat,
        lng = lng,
        lastLatLngAt = lastLatLngAt,
        certificateId = certificateId,
        schoolId = schoolId,
        certificateDate = certificateDate,
        certificateFile = certificateFile,
        photoFile = photoFile,
        signature = signature
    )
}

private fun formatPhoneNumber(phoneNumber: String): String {
    val formattedPhoneNumber = PhoneNumberUtils.formatNumber(phoneNumber, "RU")
    return formattedPhoneNumber ?: phoneNumber
}
