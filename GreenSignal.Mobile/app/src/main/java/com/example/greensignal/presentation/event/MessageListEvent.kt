package com.example.greensignal.presentation.event

sealed class MessageListEvent {
    object GetMessages: MessageListEvent()
    data class FilterChanged(val filter: String): MessageListEvent()
}
