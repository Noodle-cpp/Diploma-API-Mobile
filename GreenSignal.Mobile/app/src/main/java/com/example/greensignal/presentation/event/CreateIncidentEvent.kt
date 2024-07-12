package com.example.greensignal.presentation.event

import android.content.Context
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.google.android.gms.maps.model.LatLng

sealed class CreateIncidentEvent {
    data class DescriprionChanged(val description: String): CreateIncidentEvent()
    data class AddressChanged(val address: String): CreateIncidentEvent()
    data class CitizenPhoneChanged(val phone: String): CreateIncidentEvent()
    data class CitizenFIOChanged(val fio: String): CreateIncidentEvent()
    data class CodeChanged(val code: String): CreateIncidentEvent()
    data class OnMapClick(val latLng: LatLng, val context: Context): CreateIncidentEvent()
    data class OnKindChanged(val kind: IncidentKind): CreateIncidentEvent()
    data class GetLocation(val lat: Double, val lng: Double, val context: Context): CreateIncidentEvent()

    object NextStep: CreateIncidentEvent()
    object PrevStep: CreateIncidentEvent()
    object CreateIncident: CreateIncidentEvent()

    object GetCode: CreateIncidentEvent()
}