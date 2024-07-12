package com.example.greensignal.data.remote.dto.request

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.greensignal.domain.model.request.CreateIncidentAttachment

data class CreateIncidentAttachmentDto(
    var file: MutableState<Uri> = mutableStateOf(Uri.EMPTY),
    var description: MutableState<String> = mutableStateOf("")
)

fun CreateIncidentAttachment.toIncidentAttachmentDto(): CreateIncidentAttachmentDto {
    return CreateIncidentAttachmentDto(
        file = file,
        description = description
    )
}