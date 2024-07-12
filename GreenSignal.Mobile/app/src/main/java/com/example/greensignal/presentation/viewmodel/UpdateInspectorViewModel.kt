package com.example.greensignal.presentation.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.example.greensignal.common.Constants
import com.example.greensignal.data.remote.dto.request.toUpdateInspectorDto
import com.example.greensignal.domain.model.request.GetCode
import com.example.greensignal.domain.model.request.UpdateInspector
import com.example.greensignal.domain.model.response.toInspectorAccount
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.presentation.event.InspectorAccountEvent
import com.example.greensignal.presentation.event.UpdateInspectorEvent
import com.example.greensignal.presentation.state.UpdateInspectorState
import com.example.greensignal.presentation.use_case.validation.ValidateCertificateId
import com.example.greensignal.presentation.use_case.validation.ValidateCertificateUri
import com.example.greensignal.presentation.use_case.validation.ValidateCode
import com.example.greensignal.presentation.use_case.validation.ValidateFIO
import com.example.greensignal.presentation.use_case.validation.ValidateInspectorPhotoUri
import com.example.greensignal.presentation.use_case.validation.ValidatePhone
import com.example.greensignal.presentation.use_case.validation.ValidateSchoolId
import com.example.greensignal.util.Resource
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class UpdateInspectorViewModel @Inject constructor(
    private val prefs: SharedPreferences,
    private val validatePhone: ValidatePhone = ValidatePhone(),
    private val validateCertificateId: ValidateCertificateId = ValidateCertificateId(),
    private val validateSchoolId: ValidateSchoolId = ValidateSchoolId(),
    private val validateCertificateUri: ValidateCertificateUri = ValidateCertificateUri(),
    private val validateInspectorPhotoUri: ValidateInspectorPhotoUri = ValidateInspectorPhotoUri(),
    private val validateFIO: ValidateFIO = ValidateFIO(),
    private val inspectorRepository: InspectorRepository,
    @ApplicationContext private val context: Context
): ViewModel() {
    var state by mutableStateOf(UpdateInspectorState())
        private set

    private val validationEventChannel = Channel<UpdateInspectorViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        onGetInspectorProfile()
    }

    fun onEvent(event: UpdateInspectorEvent) {
        when (event) {
            is UpdateInspectorEvent.CertificateDateChanged -> {
                state = state.copy(updateCertificateDate = event.date)
            }
            is UpdateInspectorEvent.CertificateIdChanged -> {
                state = state.copy(updateCertificateId = event.certificateId)
            }
            is UpdateInspectorEvent.CodeChanged -> {
                state = state.copy(updateCode = event.code)
            }
            is UpdateInspectorEvent.FioChanged -> {
                state = state.copy(updateFIO = event.fio)
            }
            is UpdateInspectorEvent.PhoneChanged -> {
                state = state.copy(updatePhone = event.phone)
            }
            is UpdateInspectorEvent.SchoolIdChanged -> {
                state = state.copy(updateSchoolId = event.schoolId)
            }

            UpdateInspectorEvent.GetCode -> {
                onGetCode()
            }

            UpdateInspectorEvent.UpdateInspector -> {
                onUpdateInspector()
            }

            is UpdateInspectorEvent.UpdateInspectorCert -> {
                onUpdateInspectorCert(event.uri)
            }

            is UpdateInspectorEvent.UpdateInspectorPhoto -> {
                onUpdateInspectorPhoto(event.uri)
            }

            is UpdateInspectorEvent.UpdateInspectorSignature -> {
                onUpdateSignature(event.bitmap)
            }
        }
    }

    private fun onUpdateSignature(bitmap: Bitmap) {
        viewModelScope.launch {
            val token = prefs.getString("inspector-jwt-token", null)

            if (token != null) {

                var jwt: JWT = JWT(token)
                var inspectorId: String? = jwt.getClaim("id").asString()

                if (inspectorId != null) {

                    state = state.copy(
                        isLoading = true,
                        error = null
                    )

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val signaturePart: MultipartBody.Part =
                        MultipartBody.Part.createFormData(
                            "SignaturePhoto",
                            "signature.png",
                            RequestBody.create("image/png".toMediaType(), byteArrayOutputStream.toByteArray())
                        )

                    when (val response = inspectorRepository.updateInspectorSignature(inspectorId, signaturePart, token)) {
                        is Resource.Success -> {

                            state = state.copy(
                                isLoading = false,
                                error = null,
                                isSuccess = true
                            )

                            validationEventChannel.send(UpdateInspectorViewModel.ValidationEvent.Success)
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
    }

    private fun onUpdateInspectorCert(uri: Uri) {
        viewModelScope.launch {
            val token = prefs.getString("inspector-jwt-token", null)

            if (token != null) {

                var jwt: JWT = JWT(token)
                var inspectorId: String? = jwt.getClaim("id").asString()

                if (inspectorId != null) {

                    state = state.copy(
                        error = null
                    )

                    val certPart: MultipartBody.Part =
                        MultipartBody.Part.createFormData(
                            "CertificatePhoto",
                            "inspector_cert.png",
                            RequestBody.create("image/*".toMediaType(), uriToFile(uri)!!)
                        )

                    when (val response = inspectorRepository.updateInspectorCert(
                        inspectorId,
                        certPart,
                        token
                    )) {
                        is Resource.Success -> {
                            state = state.copy(
                                error = null,
                                updateCertificateUri = Constants.BASE_URL + "storage/download/" + response.data!!.certificateFile!!.path
                            )

                            validationEventChannel.send(UpdateInspectorViewModel.ValidationEvent.Success)
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                error = response.message!!
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onUpdateInspectorPhoto(uri: Uri) {
        viewModelScope.launch {
            val token = prefs.getString("inspector-jwt-token", null)

            if (token != null) {

                var jwt: JWT = JWT(token)
                var inspectorId: String? = jwt.getClaim("id").asString()

                if (inspectorId != null) {

                    state = state.copy(
                        error = null
                    )

                    val photoPart: MultipartBody.Part =
                        MultipartBody.Part.createFormData(
                            "InspectorPhoto",
                            "inspector_photo.png",
                            RequestBody.create("image/*".toMediaType(), uriToFile(uri)!!)
                        )

                    when (val response = inspectorRepository.updateInspectorPhoto(
                        inspectorId,
                        photoPart,
                        token
                    )) {
                        is Resource.Success -> {
                            state = state.copy(
                                error = null,
                                updatePhotoUri = Constants.BASE_URL + "storage/download/" + response.data!!.photoFile!!.path
                            )

                            validationEventChannel.send(UpdateInspectorViewModel.ValidationEvent.Success)
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                error = response.message!!
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onUpdateInspector() {
        val phoneResult = validatePhone.execute(state.updatePhone)
        val certificateIdResult = validateCertificateId.execute(state.updateCertificateId)
        val schoolIdResult = validateSchoolId.execute(state.updateSchoolId)
        val certificateUriResult = validateCertificateUri.execute(state.updateCertificateUri)
        val photoUriResult = validateInspectorPhotoUri.execute(state.updateCertificateUri)
        val fioResult = validateFIO.execute(state.updateFIO)

        val hasError = listOf(
            phoneResult,
            certificateIdResult,
            schoolIdResult,
            certificateUriResult,
            photoUriResult,
            fioResult,
        ).any { !it.isSuccessful }

        if (hasError) {
            state = state.copy(
                updatePhoneError = phoneResult.errorMessage,
                updateCertificateIdError = certificateIdResult.errorMessage,
                updateSchoolIdError = schoolIdResult.errorMessage,
                updateCertificateUriError = certificateUriResult.errorMessage,
                updatePhotoUriError = photoUriResult.errorMessage,
                updateFIOError = fioResult.errorMessage,
            )

            return
        }

        viewModelScope.launch {
            val token = prefs.getString("inspector-jwt-token", null)

            if (token != null) {

                var jwt: JWT = JWT(token)
                var inspectorId: String? = jwt.getClaim("id").asString()

                if (inspectorId != null) {

                    state = state.copy(
                        isLoading = true,
                        error = null
                    )

                    val utcDateTime = state.updateCertificateDate.atOffset(ZoneOffset.UTC)
                    val outputFormatter =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

                    val updateInspector = UpdateInspector(
                        fio = state.updateFIO,
                        phone = state.updatePhone.replace(Regex("\\D"), ""),
                        certificateId = state.updateCertificateId,
                        certificateDate = outputFormatter.format(utcDateTime),
                        schoolId = state.updateSchoolId
                    )

                    when (val response = inspectorRepository.updateInspector(
                        inspectorId,
                        updateInspector.toUpdateInspectorDto(),
                        token
                    )) {
                        is Resource.Success -> {
                            state = state.copy(
                                isLoading = false,
                                error = null,
                                isSuccess = true
                            )

                            validationEventChannel.send(UpdateInspectorViewModel.ValidationEvent.Success)
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
    }

    private fun onGetInspectorProfile() {
        val token = prefs.getString("inspector-jwt-token", null)

        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            when (val response = inspectorRepository.inspectorGetProfile(token!!)
            ) {
                is Resource.Success -> {

                    val inspector = response.data!!.toInspectorAccount()

                    state = state.copy(
                        isLoading = false,
                        error = null,
                        updateFIO = inspector.fio,
                        updatePhone = "+" + inspector.phone,
                        status = inspector.reviewStatus,
                        updateCertificateId = inspector.certificateId,
                        updateSchoolId = inspector.schoolId,
                        updatePhotoUri = if(inspector.photoFile != null) Constants.BASE_URL + "storage/download/" + inspector.photoFile.path else null,
                        updateCertificateUri = if(inspector.certificateFile != null) Constants.BASE_URL + "storage/download/" + inspector.certificateFile.path else null
                    )

                    validationEventChannel.send(UpdateInspectorViewModel.ValidationEvent.Success)
                }

                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = response.message!!,
                    )
                }
            }
        }
    }

    private fun onGetCode() {
        val phoneResult = validatePhone.execute(state.updatePhone)

        val hasError = listOf(
            phoneResult,
        ).any { !it.isSuccessful }

        if(hasError) {
            state = state.copy(
                updatePhoneError = phoneResult.errorMessage,
            )
            return
        }

        viewModelScope.launch {
            state = state.copy(
                isCodeLoading = true,
                error = null
            )

            val formattedNumber = state.updatePhone.replace(Regex("\\D"), "")
            val getCode = GetCode(
                phone = formattedNumber
            )

            when(val response = inspectorRepository.inspectorGetCode(getCode)) {
                is Resource.Success -> {

                    state = state.copy(
                        isCodeLoading = false,
                        error = null,
                        isGetCodeSuccess = true
                    )

                    validationEventChannel.send(UpdateInspectorViewModel.ValidationEvent.Success)
                }
                is Resource.Error -> {
                    state = state.copy(
                        isCodeLoading = false,
                        error = response.message!!,
                        isGetCodeSuccess = false
                    )
                }
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            return File(filePath)
        }
        return null
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}