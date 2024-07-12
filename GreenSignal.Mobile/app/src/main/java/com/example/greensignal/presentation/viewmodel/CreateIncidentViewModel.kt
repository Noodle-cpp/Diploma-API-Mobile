package com.example.greensignal.presentation.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greensignal.data.remote.dto.request.AuthorizeCitizenDto
import com.example.greensignal.domain.model.request.CreateIncident
import com.example.greensignal.domain.model.request.GetCode
import com.example.greensignal.domain.repository.CitizenRepository
import com.example.greensignal.domain.repository.IncidentAttachmentRepository
import com.example.greensignal.domain.repository.IncidentRepository
import com.example.greensignal.presentation.event.CreateIncidentEvent
import com.example.greensignal.presentation.state.CreateIncidentState
import com.example.greensignal.presentation.use_case.validation.ValidateAddress
import com.example.greensignal.presentation.use_case.validation.ValidateCode
import com.example.greensignal.presentation.use_case.validation.ValidateDescription
import com.example.greensignal.presentation.use_case.validation.ValidatePhone
import com.example.greensignal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.Locale
import javax.inject.Inject


@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@HiltViewModel
class CreateIncidentViewModel @Inject constructor(
    private val prefs: SharedPreferences,
    private val incidentRepository: IncidentRepository,
    private val incidentAttachmentRepository: IncidentAttachmentRepository,
    private val citizenRepository: CitizenRepository,
    private val validatePhone: ValidatePhone = ValidatePhone(),
    private val validateCode: ValidateCode = ValidateCode(),
    private val validateDescription: ValidateDescription = ValidateDescription(),
    private val validateAddress: ValidateAddress = ValidateAddress(),
    @ApplicationContext private val context: Context
    ) : ViewModel() {

    var state by mutableStateOf(CreateIncidentState())
        private set


    private val validationEventChannel = Channel<CreateIncidentViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: CreateIncidentEvent) {
        when (event) {
            is CreateIncidentEvent.AddressChanged -> {
                state = state.copy(address = event.address)
            }

            is CreateIncidentEvent.CitizenFIOChanged -> {
                state = state.copy(citizenFIO = event.fio)
            }

            is CreateIncidentEvent.CitizenPhoneChanged -> {
                state = state.copy(citizenPhone = formatPhoneNumber(event.phone))
            }

            is CreateIncidentEvent.CodeChanged -> {
                state = state.copy(code = event.code)
            }

            is CreateIncidentEvent.DescriprionChanged -> {
                state = state.copy(description = event.description)
            }

            CreateIncidentEvent.NextStep -> {
                onNextStep(state.step)
            }

            is CreateIncidentEvent.OnMapClick -> {
                val  address = getCompleteAddressString(event.latLng.latitude, event.latLng.longitude, event.context)

                state = state.copy(
                    lat = event.latLng.latitude,
                    lng = event.latLng.longitude,
                    address = address
                )
            }

            is CreateIncidentEvent.OnKindChanged -> {
                state = state.copy(
                    kind = event.kind
                )
            }

            CreateIncidentEvent.GetCode -> {
                onGetCode()
            }

            CreateIncidentEvent.CreateIncident -> {
                onCreateIncident()
                onNextStep(state.step)
            }

            CreateIncidentEvent.PrevStep -> {
                state = state.copy(step = state.step - 1)
            }

            is CreateIncidentEvent.GetLocation -> {
                state = state.copy(
                    lat = event.lat,
                    lng = event.lng,
                    address = getCompleteAddressString(event.lat, event.lng, event.context)
                )
            }
        }
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        val formattedPhoneNumber = PhoneNumberUtils.formatNumber(phoneNumber, "RU")
        return formattedPhoneNumber ?: phoneNumber
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double, context: Context): String {
        var strAdd = ""
        val geocoder = Geocoder(context, Locale("ru", "RU"))
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")

                strAdd = returnedAddress.countryName!! + ", " + // страна
                        returnedAddress.adminArea!! + ", " + // область
                        returnedAddress.subAdminArea!! + ", " + // город
                        returnedAddress.thoroughfare!! + ", " + // улицв
                        returnedAddress.featureName!! // номер дома

                Log.w("My Current loction address", strReturnedAddress.toString())
            } else {
                Log.w("My Current loction address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("My Current loction address", "Canont get Address!")
        }
        return strAdd
    }

    private fun onGetCode() {
        val phoneResult = validatePhone.execute(state.citizenPhone)

        val hasError = listOf(
            phoneResult,
        ).any { !it.isSuccessful }

        if (hasError) {
            state = state.copy(
                citizenPhoneError = phoneResult.errorMessage,
            )

            return
        }

        viewModelScope.launch {
            state = state.copy(
                isCodeLoading = true,
                error = null
            )

            val formattedNumber = state.citizenPhone.replace(Regex("\\D"), "")
            val getCode = GetCode(
                phone = formattedNumber
            )

            when (val response = citizenRepository.citizenGetCode(getCode)) {
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

    private fun onCreateIncident() {

        val phoneResult = validatePhone.execute(state.citizenPhone)
        val codeResult = validateCode.execute(state.code)

        val hasError = listOf(
            phoneResult,
            codeResult
        ).any { !it.isSuccessful }

        if (hasError) {
            state = state.copy(
                citizenPhoneError = phoneResult.errorMessage,
                codeError = codeResult.errorMessage
            )
            return
        }

        viewModelScope.launch {
            val formattedNumber = state.citizenPhone.replace(Regex("\\D"), "")


            state = state.copy(
                isLoading = true,
                error = null
            )

            val authorizeCitizenDto = AuthorizeCitizenDto(
                phone = formattedNumber,
                fio = state.citizenFIO,
                code = state.code
            )

            when (val response = citizenRepository.citizenLogin(authorizeCitizenDto)) {
                is Resource.Success -> {
                    val createIncident = CreateIncident(
                        description = state.description,
                        address = state.address,
                        lat = state.lat!!,
                        lng = state.lng!!,
                        kind = state.kind.index
                    )

                    val incident = incidentRepository.createIncident(createIncident, response.data!!.token)

                    if(incident.data != null) {
                        for (file in state.files) {
                            val fileImg: File? = uriToFile(file.file.value)

                            if (fileImg != null) {
                                val filePart: MultipartBody.Part =
                                    MultipartBody.Part.createFormData(
                                        "file",
                                        fileImg.getName(),
                                        RequestBody.create("image/*".toMediaType(), fileImg)
                                    )

                                val descriptionPart: RequestBody =
                                    RequestBody.create(
                                        "text/plain".toMediaType(),
                                        file.description.value
                                    )

                                incidentAttachmentRepository.attachFileToIncident(
                                    filePart,
                                    descriptionPart,
                                    incident.data.id,
                                    response.data.token
                                )
                            }
                        }

                        incidentRepository.applyIncident(incident.data.id, response.data.token)

                        state = state.copy(
                            isLoading = false,
                            error = null
                        )
                    }

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

    private fun onNextStep(step: Int) {
        when (step) {
            2 -> {
                val descriptionResult = validateDescription.execute(state.description)

                val hasError = listOf(
                    descriptionResult,
                ).any { !it.isSuccessful }

                if (hasError) {
                    state = state.copy(
                        descriptionError = descriptionResult.errorMessage,
                    )

                    return
                }
            }
            3 -> {
                val addressResult = validateAddress.execute(state.address)

                val hasError = listOf(
                    addressResult,
                ).any { !it.isSuccessful }

                if (hasError) {
                    state = state.copy(
                        addressError = addressResult.errorMessage,
                    )

                    return
                }
            }
        }

        state = state.copy(step = state.step + 1)
    }
}