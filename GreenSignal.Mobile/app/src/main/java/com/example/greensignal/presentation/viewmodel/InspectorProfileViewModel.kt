package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greensignal.common.Constants
import com.example.greensignal.domain.model.response.toInspectorAccount
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.domain.repository.StorageRepository
import com.example.greensignal.presentation.event.InspectorAccountEvent
import com.example.greensignal.presentation.state.InspectorProfileState
import com.example.greensignal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class InspectorProfileViewModel @Inject constructor(
    private val prefs: SharedPreferences,
    private val inspectorRepository: InspectorRepository,
) : ViewModel() {

    var state by mutableStateOf(InspectorProfileState())
        private set


    private val validationEventChannel = Channel<InspectorProfileViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        onGetInspectorProfile()
    }

    fun onEvent(event: InspectorAccountEvent) {
        when (event) {
            InspectorAccountEvent.Logout -> {
                onLogout()
            }

            InspectorAccountEvent.GetInspectorProfile -> {
                onGetInspectorProfile()
            }

            is InspectorAccountEvent.PhotoStateChanged -> {
                state = state.copy(isPhotoLoaded = event.isLoaded)
            }

            InspectorAccountEvent.UpdateInspectorProfilePhoto -> {
                val url = state.photoFile
                state = state.copy(photoFile = "$url?")
            }

            InspectorAccountEvent.UpdateInspectorProfileCert -> {
                val url = state.certFile
                state = state.copy(certFile = "$url?")
            }

            is InspectorAccountEvent.CertStateChanged -> {
                state = state.copy(isCertLoaded = event.isLoaded)
            }

            is InspectorAccountEvent.SignatureStateChanged -> {
                state = state.copy(isSignatureLoaded = event.isLoaded)
            }
        }
    }

    private fun onGetInspectorProfile() {
        val token = prefs.getString("inspector-jwt-token", null)
        state = state.copy(
            isLoading = true,
            error = null
        )

        if(token != null) {
            viewModelScope.launch {
                when (val response = inspectorRepository.inspectorGetProfile(token!!)
                ) {
                    is Resource.Success -> {

                        val inspector = response.data!!.toInspectorAccount()

                        val localDate = response.data.certificateDate!!.toInstant()
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        val outputFormatter =
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")

                        state = state.copy(
                            isLoading = false,
                            error = null,
                            fio = inspector.fio,
                            phone = inspector.phone,
                            status = inspector.reviewStatus,
                            certificateId = inspector.certificateId,
                            schoolId = inspector.schoolId,
                            certificateDate = outputFormatter.format(localDate),
                            photoFile = if (inspector.photoFile != null) Constants.BASE_URL + "storage/download/" + inspector.photoFile.path else null,
                            certFile = if (inspector.certificateFile != null) Constants.BASE_URL + "storage/download/" + inspector.certificateFile.path else null,
                            signatureFile = if (inspector.signature != null) Constants.BASE_URL + "storage/download/" + inspector.signature.path else null
                        )

                        validationEventChannel.send(InspectorProfileViewModel.ValidationEvent.Success)
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = response.message!!,
                        )
                    }
                }
            }
        } else {
            state = state.copy(
                isLoading = false,
                error = null,
                isAuthorized = false
            )
        }
    }

    private fun onLogout() {
        val token = prefs.getString("inspector-jwt-token", null)

        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            when(val response = inspectorRepository.inspectorLogout(token!!)
            ) {
                is Resource.Success -> {

                    state = state.copy(
                        isLoading = false,
                        error = null,
                    )

                    validationEventChannel.send(InspectorProfileViewModel.ValidationEvent.Success)
                }

                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = response.message!!,
                    )
                }
            }
        }

        prefs.edit()
            .putString("inspector-jwt-token", null)
            .apply()
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}