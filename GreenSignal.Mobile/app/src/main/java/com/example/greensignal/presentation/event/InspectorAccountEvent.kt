package com.example.greensignal.presentation.event

sealed class InspectorAccountEvent {
    object Logout: InspectorAccountEvent()
    object GetInspectorProfile: InspectorAccountEvent()
    data class PhotoStateChanged(val isLoaded: Boolean): InspectorAccountEvent()
    data class CertStateChanged(val isLoaded: Boolean): InspectorAccountEvent()
    data class SignatureStateChanged(val isLoaded: Boolean): InspectorAccountEvent()
    object UpdateInspectorProfilePhoto: InspectorAccountEvent()
    object UpdateInspectorProfileCert: InspectorAccountEvent()

}