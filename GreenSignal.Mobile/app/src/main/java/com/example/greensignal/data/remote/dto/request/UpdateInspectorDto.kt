package com.example.greensignal.data.remote.dto.request

import com.example.greensignal.data.remote.dto.response.CitizenDto
import com.example.greensignal.domain.model.request.UpdateInspector
import com.example.greensignal.domain.model.response.Citizen

data class UpdateInspectorDto(
    val fio: String = "",
    val phone: String = "",
    val certificateId: String = "",
    val certificateDate: String = "",
    val schoolId: String = ""
)

fun UpdateInspector.toUpdateInspectorDto(): UpdateInspectorDto {
    return  UpdateInspectorDto(
        fio = fio,
        phone = phone,
        certificateId = certificateId,
        certificateDate = certificateDate,
        schoolId = schoolId
    )
}