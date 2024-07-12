package com.example.greensignal.presentation.event

import android.content.Context
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

sealed class UpdateIncidentReportEvent {
    data class DescriptionChanged(val description: String): UpdateIncidentReportEvent()
    object UpdateIncidentReport: UpdateIncidentReportEvent()
    object GetIncidentList: UpdateIncidentReportEvent()
    object GetAttributesItems: UpdateIncidentReportEvent()
    data class IsSelectedIncidentKindChanged(val incidentReportKind: IncidentReportKind): UpdateIncidentReportEvent()
    data class DateChanged(val date: LocalDateTime, val dateType: Int): UpdateIncidentReportEvent()
    data class TimeChanged(val hour: Int, val minute: Int, val dateType: Int): UpdateIncidentReportEvent()
    data class TerritoryDescriprionChanged(val description: String): UpdateIncidentReportEvent()
    data class СadastralТumberChanged(val cadastralNumber: String): UpdateIncidentReportEvent()
    data class IsBarrierChanged(val isBarrier: Boolean): UpdateIncidentReportEvent()
    data class SourceDescriptionChanged(val description: String): UpdateIncidentReportEvent()
    data class FuelTypeChanged(val fuelType: String): UpdateIncidentReportEvent()
    data class EventsChanged(val events: String): UpdateIncidentReportEvent()
    data class CountOfStumpsChanged(val events: String): UpdateIncidentReportEvent()
    data class DiameterChanged(val diameter: String): UpdateIncidentReportEvent()
    data class DrivewaysChanged(val driveways: Boolean): UpdateIncidentReportEvent()
    data class SquareChanged(val square: String): UpdateIncidentReportEvent()
    data class TypeOfMiniralChanged(val typeOfMiniral: String): UpdateIncidentReportEvent()
    data class VolumeChanged(val volume: String): UpdateIncidentReportEvent()
    data class WoodTypeChanged(val woodType: String): UpdateIncidentReportEvent()
    data class AddressChanged(val address: String): UpdateIncidentReportEvent()
    data class OnMapClick(val latLng: LatLng, val context: Context): UpdateIncidentReportEvent()
    data class OnMarkerClick(val latLng: LatLng): UpdateIncidentReportEvent()
    data class GetIncident(val id: String?): UpdateIncidentReportEvent()
    data class GetIncidentReport(val id: String?): UpdateIncidentReportEvent()
    data class AddFileIdOnRemoveList(val id: String): UpdateIncidentReportEvent()
}