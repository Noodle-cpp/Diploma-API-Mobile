package com.example.greensignal.domain.model.interfaces

import android.net.Uri
import androidx.compose.runtime.MutableState

interface IIncidentReportAttachment {
    var file: MutableState<Uri>
    var description: MutableState<String>
    var manualDate: MutableState<String>
}