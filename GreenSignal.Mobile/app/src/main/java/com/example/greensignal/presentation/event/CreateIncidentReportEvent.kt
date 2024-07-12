package com.example.greensignal.presentation.event

import android.content.Context
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

sealed class CreateIncidentReportEvent {
    data class DescriptionChanged(val description: String): CreateIncidentReportEvent()
    data class IsSelectedIncidentKindChanged(val incidentReportKind: IncidentReportKind): CreateIncidentReportEvent()
    data class DateChanged(val date: LocalDateTime, val dateType: Int): CreateIncidentReportEvent()
    data class TimeChanged(val hour: Int, val minute: Int, val dateType: Int): CreateIncidentReportEvent()
    data class TerritoryDescriprionChanged(val description: String): CreateIncidentReportEvent()
    data class СadastralТumberChanged(val cadastralNumber: String): CreateIncidentReportEvent()
    data class IsBarrierChanged(val isBarrier: Boolean): CreateIncidentReportEvent()
    data class TrashContentChanged(val source_of_pollution: String): CreateIncidentReportEvent()
    data class SourceDescriptionChanged(val description: String): CreateIncidentReportEvent()
    data class FuelTypeChanged(val fuelType: String): CreateIncidentReportEvent()
    data class EventsChanged(val events: String): CreateIncidentReportEvent()
    data class CountOfStumpsChanged(val events: String): CreateIncidentReportEvent()
    data class DiameterChanged(val diameter: String): CreateIncidentReportEvent()
    data class DrivewaysChanged(val driveways: Boolean): CreateIncidentReportEvent()
    data class SquareChanged(val square: String): CreateIncidentReportEvent()
    data class TypeOfMiniralChanged(val typeOfMiniral: String): CreateIncidentReportEvent()
    data class VolumeChanged(val volume: String): CreateIncidentReportEvent()
    data class WoodTypeChanged(val woodType: String): CreateIncidentReportEvent()
    object CreateIncidentReport: CreateIncidentReportEvent()
    object GetIncidentList: CreateIncidentReportEvent()
    object GetAttributesItems: CreateIncidentReportEvent()
    data class AddressChanged(val address: String): CreateIncidentReportEvent()
    data class OnMapClick(val latLng: LatLng, val context: Context): CreateIncidentReportEvent()
    data class OnMarkerClick(val latLng: LatLng): CreateIncidentReportEvent()
    data class GetLocation(val lat: Double, val lng: Double, val context: Context): CreateIncidentReportEvent()
    data class GetIncident(val id: String?): CreateIncidentReportEvent()
}