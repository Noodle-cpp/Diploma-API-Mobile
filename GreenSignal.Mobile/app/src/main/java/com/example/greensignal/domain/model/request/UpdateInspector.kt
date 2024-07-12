package com.example.greensignal.domain.model.request

import com.example.greensignal.data.remote.dto.request.UpdateInspectorDto
import okhttp3.RequestBody
import java.util.Date

data class UpdateInspector(
    val fio: String = "",
    val phone: String = "",
    val certificateId: String = "",
    val certificateDate: String = "",
    val schoolId: String = ""
)
