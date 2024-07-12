package com.example.greensignal.presentation.state

import androidx.compose.runtime.mutableStateListOf
import androidx.paging.PagingData
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.domain.model.request.CreateIncidentReportAttachment
import com.example.greensignal.domain.model.response.Incident
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.time.LocalDateTime

data class CreateIncidentReportState (
    val incidentReportId: String? = null,

    val manualDate: LocalDateTime = LocalDateTime.now(),
    val startOfInspection: LocalDateTime = LocalDateTime.now(),
    val endOfInspection: LocalDateTime = LocalDateTime.now(),
    val address: String = "",
    val kind: IncidentReportKind = IncidentReportKind.AirPollution,
    val incidentId: String? = null,
    val locationId: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val attributesVersion: Int = 1,
    val territoryDescription: String = "",
    val events: String = "",
    val files: MutableList<CreateIncidentReportAttachment> = mutableStateListOf(),
    var selectedText: String = IncidentReportKind.values()[0].title,
    val selectedIncident: Incident? = null,
    val incidents: Flow<PagingData<Incident>> = emptyFlow(),

    val cadastralNumber: String = "",
    val isBarrier: Boolean = false,
    val trashContent: String = "",
    val sourceDescription: String = "",
    val fuelType: String = "",
    val points:List<LatLng> = mutableStateListOf(),
    val countOfLatElements: Int = 0,
    val countOfLngElements: Int = 0,
    val driveways: Boolean = false,
    val square: String = "",
    val volume: String = "",
    val typeOfMiniral: String = "",
    val woodType: String = "",
    val countOfStumps: String = "",
    val diameter: String = "",
    val description: String = "",

    val cadastralNumberError: String? = null,
    val sourceOfPollutionError: String? = null,
    val sourceDescriptionError: String? = null,
    val fuelTypeError: String? = null,
    val coordsError: String? = null,
    val addressError: String? = null,
    val eventsError: String? = null,
    val territoryDescriptionError: String? = null,
    val drivewaysError: String? = null,
    val squareError: String? = null,
    val volumeError: String? = null,
    val typeOfMiniralError: String? = null,
    val woodTypeError: String? = null,
    val countOfStumpsError: String? = null,
    val diameterError: String? = null,
    val descriptionError: String? = null,

    val territoryDescriptionIsVisible: Boolean = false,
    val eventsIsVisible: Boolean = false,
    val cadastralNumberIsVisible: Boolean = false,
    val isBarrierIsVisible: Boolean = false,
    val trashContentIsVisible: Boolean = false,
    val sourceDescriptionIsVisible: Boolean = false,
    val fuelTypeIsVisible: Boolean = false,
    val coordsPoligonIsVisible: Boolean = false,
    val drivewaysIsVisible: Boolean = false,
    val squareIsVisible: Boolean = false,
    val volumeIsVisible: Boolean = false,
    val typeOfMiniralIsVisible: Boolean = false,
    val woodTypeIsVisible: Boolean = false,
    val countOfStumpsIsVisible: Boolean = false,
    val diameterIsVisible: Boolean = false,

    val cadastralNumberIsRequired: Boolean = false,
    val isBarrierIsRequired: Boolean = false,
    val sourceOfPollutionIsRequired: Boolean = false,
    val sourceDescriptionIsRequired: Boolean = false,
    val fuelTypeIsRequired: Boolean = false,
    val eventsIsRequired: Boolean = false,
    val territoryDescriptionIsRequired: Boolean = false,
    val drivewaysIsRequired: Boolean = false,
    val squareIsRequired: Boolean = false,
    val volumeIsRequired: Boolean = false,
    val woodTypeIsRequired: Boolean = false,
    val typeOfMineralIsRequired: Boolean = false,
    val countOfStumpsIsRequired: Boolean = false,
    val diameterIsRequired: Boolean = false,

    val isLoading: Boolean = false,
    val isIncidentReportCreated: Boolean = false,
    val error: String? = null
)