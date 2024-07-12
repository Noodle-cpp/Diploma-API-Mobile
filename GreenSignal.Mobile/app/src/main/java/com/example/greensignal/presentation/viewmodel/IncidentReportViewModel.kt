package com.example.greensignal.presentation.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greensignal.common.Constants
import com.example.greensignal.domain.model.response.toIncidentReport
import com.example.greensignal.domain.repository.IncidentReportRepository
import com.example.greensignal.presentation.event.IncidentReportEvent
import com.example.greensignal.presentation.state.IncidentReportState
import com.example.greensignal.util.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class IncidentReportViewModel @Inject constructor(
    private val incidentReportRepository: IncidentReportRepository,
    private val prefs: SharedPreferences
): ViewModel() {
    var state by mutableStateOf(IncidentReportState())
        private set

    fun onEvent(event: IncidentReportEvent) {
        when(event) {
            is IncidentReportEvent.GetIncidentReport -> {
                onGetIncidentReport(event.id)
            }

            is IncidentReportEvent.DownloadPdf -> {
                onDownloadPdf(event.context, state.incidentReport.id)
            }

            IncidentReportEvent.ArchiveIncidentReport -> {
                onArchiveIncidentReport(state.incidentReport.id)
            }
        }
    }

    private fun onDownloadPdf(baseActivity: Context, id: String): Long? {
        val token = prefs.getString("inspector-jwt-token", null)
        if (token != null) {
            val direct = File(Environment.getExternalStorageDirectory().toString() + "/GreenSignal")
            val url = Constants.BASE_URL + "IncidentReports/${id}/pdf"
            val title =
                "Акт №${state.incidentReport.serialNumber} - ${state.incidentReport.kind.title}.pdf"

            if (!direct.exists()) {
                direct.mkdirs()
            }
            url?.substring(url.lastIndexOf("."))
            val downloadReference: Long
            val dm: DownloadManager =
                baseActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                title
            )

            request.addRequestHeader("Authorization", token)

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle(title)
            Toast.makeText(baseActivity, "Начинаю скачивание..", Toast.LENGTH_SHORT).show()

            downloadReference = dm.enqueue(request)

            return downloadReference
        } else return null
    }

    private fun onGetIncidentReport(incidentReportId: String) {
        val token = prefs.getString("inspector-jwt-token", null)
        state = state.copy(
            isLoading = true,
            error = null,
        )

        if (token != null) {
            viewModelScope.launch {
                when (val response = incidentReportRepository.getIncidentReport(incidentReportId, token)) {
                    is Resource.Success -> {
                        if (response.data != null) {
                            val points: MutableList<LatLng> = mutableListOf()

                            val geoRegex = Regex("GEO_(\\d+)_LAT")
                            val latMap = response.data.incidentReportAttributes.filter { it.name.matches(geoRegex) }
                                .associateBy({ it.name }, { it.numberValue })

                            val lngMap = response.data.incidentReportAttributes.filter { it.name.matches(Regex("GEO_(\\d+)_LNG")) }
                                .associateBy({ it.name }, { it.numberValue })

                            latMap.keys.forEach { latKey ->
                                val pointNumber = latKey.replace("GEO_", "").replace("_LAT", "")
                                val latValue = latMap[latKey]
                                val lngValue = lngMap["GEO_${pointNumber}_LNG"]

                                if (latValue != null && lngValue != null) {
                                    points.add(LatLng(latValue, lngValue))
                                }
                            }

                            state = state.copy(
                                incidentReport = response.data.toIncidentReport(),
                                points = points
                            )
                        }
                        state = state.copy(
                            isLoading = false,
                            error = null,
                        )
                        validationEventChannel.send(ValidationEvent.Success)
                    }


                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = response.message!!,
                        )
                    }
                }
            }
        }
    }

    private fun onArchiveIncidentReport(incidentReportId: String) {
        val token = prefs.getString("inspector-jwt-token", null)
        state = state.copy(
            isRemoveLoading = true,
            removeError = null,
        )

        if (token != null) {
            viewModelScope.launch {
                when (val response = incidentReportRepository.deleteIncidentReport(incidentReportId, token)) {
                    is Resource.Success -> {
                        state = state.copy(
                            isRemoveLoading = false,
                            removeError = null,
                        )
                        validationEventChannel.send(ValidationEvent.Success)
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            isRemoveLoading = false,
                            removeError = response.message!!,
                        )
                    }
                }
            }
        }
    }

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }
}