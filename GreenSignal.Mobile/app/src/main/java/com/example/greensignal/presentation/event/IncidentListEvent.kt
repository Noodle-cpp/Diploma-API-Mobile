package com.example.greensignal.presentation.event

import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentStatus

sealed class IncidentListEvent {
    data class TabChanged(val index: Int): IncidentListEvent()
    data class IsNerbyChanged(val isChecked: Boolean): IncidentListEvent()
    data class IsSelectedIncidentKindChanged(val incidentKind: IncidentKind?): IncidentListEvent()
    data class IsSelectedIncidentStatusChanged(val incidentStatus: IncidentStatus?): IncidentListEvent()
    data class AddressChanged(val address: String): IncidentListEvent()
    object GetIncidentList: IncidentListEvent()
}