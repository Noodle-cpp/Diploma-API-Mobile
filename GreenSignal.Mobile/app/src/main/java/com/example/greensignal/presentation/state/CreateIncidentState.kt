package com.example.greensignal.presentation.state

import androidx.compose.runtime.mutableStateListOf
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.domain.model.request.CreateIncidentAttachment

data class CreateIncidentState (
    val description: String = "",
    val descriptionError: String? = null,

    val address: String = "",
    val addressError: String? = null,

    val lat: Double? = null,
    val lng: Double? = null,

    val kind: IncidentKind = IncidentKind.Dump,

    val citizenFIO: String = "",
    val citizenFIOError: String? = null,

    val citizenPhone: String = "+7 ",
    val citizenPhoneError: String? = null,

    val files: MutableList<CreateIncidentAttachment> = mutableStateListOf(),

    val code: String = "",
    val isGetCodeSuccess: Boolean = false,
    val codeError: String? = null,
    val isCodeLoading: Boolean = false,

    val step: Int = 1,

    val isLoading: Boolean = false,
    var error: String? = null,
)

