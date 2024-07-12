package com.example.greensignal.domain.model.request

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.greensignal.data.remote.dto.request.CreateIncidentAttachmentDto

data class CreateIncidentAttachment(
    var file: MutableState<Uri> = mutableStateOf(Uri.EMPTY),
    var description: MutableState<String> = mutableStateOf("")
)


