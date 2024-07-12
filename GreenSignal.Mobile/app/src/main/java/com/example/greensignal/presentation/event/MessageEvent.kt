package com.example.greensignal.presentation.event

import android.content.Context
import com.example.greensignal.domain.model.response.Petition
import com.example.greensignal.domain.model.response.SavedFile

sealed class MessageEvent {
    data class GetMessage(val id: String): MessageEvent()
    data class CheckPetition(val petition: Petition?): MessageEvent()
    data class AttachPetition(val petition: Petition?): MessageEvent()
    data class DownloadFile(val context: Context, val savedFile: SavedFile): MessageEvent()
}