package com.example.greensignal.presentation.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.example.greensignal.domain.model.request.AuthorizeInspector
import com.example.greensignal.domain.model.request.GetCode
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.presentation.event.AuthenticationEvent
import com.example.greensignal.presentation.state.AuthenticationState
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val inspectorRepository: InspectorRepository,
    private val validatePhone: ValidatePhone = ValidatePhone(),
    private val validateCode: ValidateCode = ValidateCode(),
    private val validateCertificateId: ValidateCertificateId = ValidateCertificateId(),
    private val validateSchoolId: ValidateSchoolId = ValidateSchoolId(),
    private val validateCertificateUri: ValidateCertificateUri = ValidateCertificateUri(),
    private val validateInspectorPhotoUri: ValidateInspectorPhotoUri = ValidateInspectorPhotoUri(),
    private val validateFIO: ValidateFIO = ValidateFIO(),
    private val prefs: SharedPreferences,
    @ApplicationContext private val context: Context
): ViewModel() {
    var state by mutableStateOf(AuthenticationState())
        private set

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: AuthenticationEvent) {
        when(event) {
            is AuthenticationEvent.CodeChanged -> {
                if(state.selectedTabIndex == 0) state = state.copy(loginCode = event.code)
                else if(state.selectedTabIndex == 1) state = state.copy(registrationCode = formatPhoneNumber(event.code))
            }

            AuthenticationEvent.GetCode -> {
                if(state.selectedTabIndex == 0) onGetCode(state.loginPhone, false)
                else if(state.selectedTabIndex == 1) onGetCode(state.registrationPhone, true)
            }

            AuthenticationEvent.Authentication -> {
                if(state.selectedTabIndex == 0) onAuthentication(state.loginPhone, state.loginCode)
                else if(state.selectedTabIndex == 1) onAuthentication(state.registrationPhone, state.registrationCode)
            }

            is AuthenticationEvent.PhoneChanged -> {
                if(state.selectedTabIndex == 0) state = state.copy(loginPhone = formatPhoneNumber(event.phone))
                else if(state.selectedTabIndex == 1) state = state.copy(registrationPhone = formatPhoneNumber(event.phone))
            }

            is AuthenticationEvent.TabChanged -> {
                state = state.copy(selectedTabIndex = event.index)
            }

            is AuthenticationEvent.CertificateIdChanged -> {
                state = state.copy(registrationCertificateId = event.certificateId)
            }

            is AuthenticationEvent.SchoolIdChanged -> {
                state = state.copy(registrationSchoolId = event.schoolId)
            }

            is AuthenticationEvent.CertificateDateChanged -> {
                state = state.copy(registrationCertificateDate = event.date)
            }

            is AuthenticationEvent.CreateInspector -> {
                onRegistration(event.bitmap)
            }

            is AuthenticationEvent.FioChanged -> {
                state = state.copy(registrationFIO = event.fio)
            }
        }
    }

    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }

    private fun onGetCode(phone: String, isRegistration: Boolean) {
        val phoneResult = validatePhone.execute(phone)

        val hasError = listOf(
            phoneResult,
        ).any { !it.isSuccessful }

        if(hasError) {
            state = state.copy(
                loginPhoneError = phoneResult.errorMessage,
            )
            return
        }

        viewModelScope.launch {
            state = state.copy(
                isCodeLoading = true,
                error = null
            )

            val formattedNumber = phone.replace(Regex("\\D"), "")
            val getCode = GetCode(
                phone = formattedNumber
            )

            when(val response = if(!isRegistration) inspectorRepository.inspectorGetCode(getCode)
                                                else inspectorRepository.registrationInspectorGetCode(getCode)) {
                is Resource.Success -> {

                    state = state.copy(
                        isCodeLoading = false,
                        error = null,
                        isGetCodeSuccess = true
                    )

                    validationEventChannel.send(ValidationEvent.Success)
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

    private fun onAuthentication(phone: String, code: String) {
        val phoneResult = validatePhone.execute(phone)
        val codeResult = validateCode.execute(code)

        val hasError = listOf(
            phoneResult,
            codeResult
        ).any { !it.isSuccessful }

        if(hasError) {
            state = state.copy(
                loginPhoneError = phoneResult.errorMessage,
                loginCodeError =  codeResult.errorMessage
            )

            return
        }

        val formattedNumber = phone.replace(Regex("\\D"), "")
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            val firebaseToken = withContext(Dispatchers.IO) {
                FirebaseMessaging.getInstance().token.await()
            }

            val authorizeInspector = AuthorizeInspector(
                phone = formattedNumber,
                code = code,
                deviceName = Build.MODEL,
                firebaseToken = firebaseToken
            )

            when(val response = inspectorRepository.inspectorLogin(authorizeInspector)) {
                is Resource.Success -> {
                    prefs.edit()
                        .putString("inspector-jwt-token", response.data!!.token)
                        .apply()


                    state = state.copy(
                        isLoading = false,
                        error = null,
                        isAuthorize = true
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

    private fun onRegistration(bitmap: Bitmap) {
        val phoneResult = validatePhone.execute(state.registrationPhone)
        val codeResult = validateCode.execute(state.registrationCode)
        val certificateIdResult = validateCertificateId.execute(state.registrationCertificateId)
        val schoolIdResult = validateSchoolId.execute(state.registrationSchoolId)
        val certificateUriResult = validateCertificateUri.execute(state.registrationCertificateUri.value)
        val photoUriResult = validateInspectorPhotoUri.execute(state.registrationPhotoUri.value)
        val fioResult = validateFIO.execute(state.registrationFIO)

        val hasError = listOf(
            phoneResult,
            codeResult,
            certificateIdResult,
            schoolIdResult,
            certificateUriResult,
            photoUriResult,
            fioResult,
        ).any { !it.isSuccessful }

        if(hasError) {
            state = state.copy(
                registrationPhoneError = phoneResult.errorMessage,
                registrationCodeError =  codeResult.errorMessage,
                registrationCertificateIdError = certificateIdResult.errorMessage,
                registrationSchoolIdError = schoolIdResult.errorMessage,
                registrationCertificateUriError = certificateUriResult.errorMessage,
                registrationPhotoUriError = photoUriResult.errorMessage,
                registrationFIOError = fioResult.errorMessage,
            )

            return
        }

        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            val fioPart: RequestBody =
                RequestBody.create(
                    "text/plain".toMediaType(),
                    state.registrationFIO
                )
            val phonePart: RequestBody =
                RequestBody.create(
                    "text/plain".toMediaType(),
                    state.registrationPhone.replace(Regex("\\D"), "")
                )
            val certificateIdPart: RequestBody =
                RequestBody.create(
                    "text/plain".toMediaType(),
                    state.registrationCertificateId
                )

            val utcDateTime = state.registrationCertificateDate.atOffset(ZoneOffset.UTC)
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

            val certificateDatePart: RequestBody =
                RequestBody.create(
                    "text/plain".toMediaType(),
                    outputFormatter.format(utcDateTime)

                )
            val schoolIdPart: RequestBody =
                RequestBody.create(
                    "text/plain".toMediaType(),
                    state.registrationSchoolId
                )
            val codePart: RequestBody =
                RequestBody.create(
                    "text/plain".toMediaType(),
                    state.registrationCode
                )
            val firebasePart: RequestBody =
                RequestBody.create(
                    "text/plain".toMediaType(),
                    withContext(Dispatchers.IO) {
                        FirebaseMessaging.getInstance().token.await()
                    }
                )
            val devicePart: RequestBody =
                RequestBody.create(
                    "text/plain".toMediaType(),
                    Build.MODEL
                )
            val photoPart: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "InspectorPhoto",
                    "inspector_photo.png",
                    RequestBody.create("image/*".toMediaType(), uriToFile(state.registrationPhotoUri.value)!!)
                )

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val signaturePart: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "SignaturePhoto",
                    "signature.png",
                    RequestBody.create("image/png".toMediaType(), byteArrayOutputStream.toByteArray())
                )

            val certificatePart: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "CertificatePhoto",
                    "certificate.png",
                    RequestBody.create("image/*".toMediaType(), uriToFile(state.registrationCertificateUri.value)!!)
                )

            when(val response = inspectorRepository.createInspector(
                fioPart,
                phonePart,
                certificateIdPart,
                certificateDatePart,
                schoolIdPart,
                codePart,
                firebasePart,
                devicePart,
                photoPart,
                certificatePart
                )) {
                is Resource.Success -> {

                    prefs.edit()
                        .putString("inspector-jwt-token", response.data!!.token)
                        .apply()

                    val jwt: JWT = JWT(response.data.token)
                    val inspectorId: String? = jwt.getClaim("id").asString()

                    inspectorRepository.updateInspectorSignature(inspectorId!!, signaturePart, response.data.token)

                    state = state.copy(
                        isLoading = false,
                        error = null,
                        isAuthorize = true,
                    )

                    validationEventChannel.send(ValidationEvent.Success)
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

    private fun formatPhoneNumber(phoneNumber: String): String {
        val formattedPhoneNumber = PhoneNumberUtils.formatNumber(phoneNumber, "RU")
        return formattedPhoneNumber ?: phoneNumber
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
}