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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.auth0.android.jwt.JWT
import com.example.greensignal.common.Constants
import com.example.greensignal.domain.model.response.Petition
import com.example.greensignal.domain.model.response.toMessage
import com.example.greensignal.domain.model.response.toPetition
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.domain.repository.PetitionRepository
import com.example.greensignal.presentation.event.MessageEvent
import com.example.greensignal.presentation.state.MessageState
import com.example.greensignal.presentation.use_case.pagination.PetitionListPagination
import com.example.greensignal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val inspectorRepository: InspectorRepository,
    private val petitionRepository: PetitionRepository,
    private val prefs: SharedPreferences
): ViewModel() {
    var state by mutableStateOf(MessageState())
        private set

    private val validationEventChannel = Channel<MessageViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        getPetitionPagination()
    }

    fun onEvent(event: MessageEvent) {
        when(event) {
            is MessageEvent.GetMessage -> {
                onGetMessage(event.id)
            }

            is MessageEvent.CheckPetition -> {
                state = state.copy(petition = event.petition)
            }

            is MessageEvent.DownloadFile -> {
                downloadPdf(event.context, Constants.BASE_URL + "storage/download/" + event.savedFile.path, event.savedFile.origName)
            }

            is MessageEvent.AttachPetition -> {
                onAttachPetition(event.petition)
            }
        }
    }

    private fun getPetitionPagination() {
        state = state.copy(petitions = Pager(PagingConfig(pageSize = 5)) {
            PetitionListPagination(petitionRepository, prefs)
        }.flow)
    }

    private  fun onGetMessage(id: String) {
        val token = prefs.getString("inspector-jwt-token", null)

        if(token != null)
        {
            val jwt: JWT = JWT(token)
            val inspectorId: String? = jwt.getClaim("id").asString()

            viewModelScope.launch {
                state = state.copy(
                    isLoading = true,
                    error = null
                )

                when(val response = inspectorRepository.setMessageAsSeen(inspectorId!!, id, token)) {
                    is Resource.Success -> {
                        state = state.copy(
                            message = response.data!!.toMessage()
                        )

                        if(state.message.petitionId != null) {
                            val petition =
                                petitionRepository.getPetition(state.message.petitionId!!, token)

                            state = state.copy(
                                petition = petition.data!!.toPetition()
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
                            error = response.message!!
                        )
                    }
                }
            }
        }
    }

    private  fun onAttachPetition(petition: Petition?) {
        val token = prefs.getString("inspector-jwt-token", null)

        if(token != null)
        {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true,
                    error = null
                )

                when(val response =
                    if(state.message.petitionId != null) petitionRepository.detachMessageFromPetition(state.message.petitionId!!, state.message.id, token)
                    else Resource.Success()) {

                    is Resource.Success -> {

                        petitionRepository.attachMessageToPetition(petition!!.id, state.message.id, token)

                        state = state.copy(
                            isLoading = false,
                            error = null,
                            petition = petition
                        )

                        validationEventChannel.send(ValidationEvent.Success)
                    }
                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = response.message!!
                        )
                    }
                }
            }
        }
    }

    private fun downloadPdf(baseActivity: Context, url: String?, title: String?): Long {
        val direct = File(Environment.getExternalStorageDirectory().toString() + "/GreenSignal")

        if (!direct.exists()) {
            direct.mkdirs()
        }
        url?.substring(url.lastIndexOf("."))
        val downloadReference: Long
        val dm: DownloadManager = baseActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            title
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setTitle(title)
        Toast.makeText(baseActivity, "Начинаю скачивание..", Toast.LENGTH_SHORT).show()

        downloadReference = dm.enqueue(request)

        return downloadReference
    }

    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }
}