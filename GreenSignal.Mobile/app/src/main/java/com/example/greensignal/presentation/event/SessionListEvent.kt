package com.example.greensignal.presentation.event

sealed class SessionListEvent {
    data class RemoveSession(val id: String): SessionListEvent()
}