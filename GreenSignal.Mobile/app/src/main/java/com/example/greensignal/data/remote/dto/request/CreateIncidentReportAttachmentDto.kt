package com.example.greensignal.data.remote.dto.request

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.greensignal.domain.model.request.CreateIncidentReportAttachment

data class CreateIncidentReportAttachmentDto(
    var file: MutableState<Uri> = mutableStateOf(Uri.EMPTY),
    var description: MutableState<String> = mutableStateOf(""),
    var manualDate: MutableState<String> = mutableStateOf("")
)

fun CreateIncidentReportAttachment.toCreateIncidentReportAttachmentDto(): CreateIncidentReportAttachmentDto {
    return CreateIncidentReportAttachmentDto (
        file = file,
        description = description,
        manualDate = manualDate
    )
}