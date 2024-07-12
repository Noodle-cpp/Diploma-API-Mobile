package com.example.greensignal.domain.model.request

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.greensignal.domain.model.interfaces.IIncidentReportAttachment

data class UpdateIncidentReportAttachment(
    var id: String? = null,
    override var file: MutableState<Uri> = mutableStateOf(Uri.EMPTY),
    override var description: MutableState<String> = mutableStateOf(""),
    override var manualDate: MutableState<String> = mutableStateOf("")
): IIncidentReportAttachment
