package com.example.greensignal.presentation.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.greensignal.common.Constants
import com.example.greensignal.data.remote.dto.request.UpdateIncidentReportAttributeDto
import com.example.greensignal.data.remote.dto.request.toCreateIncidentReportDto
import com.example.greensignal.data.remote.dto.request.toUpdateIncidentReportAttributeDto
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.domain.model.request.CreateIncidentReport
import com.example.greensignal.domain.model.request.CreateIncidentReportAttachment
import com.example.greensignal.domain.model.request.UpdateIncidentReportAttribute
import com.example.greensignal.domain.model.response.toIncident
import com.example.greensignal.domain.repository.IncidentReportRepository
import com.example.greensignal.domain.repository.IncidentRepository
import com.example.greensignal.presentation.event.CreateIncidentReportEvent
import com.example.greensignal.presentation.state.CreateIncidentReportState
import com.example.greensignal.presentation.use_case.pagination.IncidentListPagination
import com.example.greensignal.presentation.use_case.validation.ValidateAddress
import com.example.greensignal.presentation.use_case.validation.ValidateCadastralNumber
import com.example.greensignal.presentation.use_case.validation.ValidateCountOfStumps
import com.example.greensignal.presentation.use_case.validation.ValidateDescription
import com.example.greensignal.presentation.use_case.validation.ValidateDiameter
import com.example.greensignal.presentation.use_case.validation.ValidateEvents
import com.example.greensignal.presentation.use_case.validation.ValidateFuelType
import com.example.greensignal.presentation.use_case.validation.ValidateSourceDescription
import com.example.greensignal.presentation.use_case.validation.ValidateSquare
import com.example.greensignal.presentation.use_case.validation.ValidateTrashContent
import com.example.greensignal.presentation.use_case.validation.ValidateTerritoryDescription
import com.example.greensignal.presentation.use_case.validation.ValidateTypeOfMiniral
import com.example.greensignal.presentation.use_case.validation.ValidateVolume
import com.example.greensignal.presentation.use_case.validation.ValidateWoodType
import com.example.greensignal.util.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import com.google.maps.android.SphericalUtil
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.time.Duration

@HiltViewModel
class CreateIncidentReportViewModel @Inject constructor(
    private val prefs: SharedPreferences,
    private val incidentReportRepository: IncidentReportRepository,
    private val incidentRepository: IncidentRepository,
    private val validateCadastralNumber: ValidateCadastralNumber = ValidateCadastralNumber(),
    private val validateFuelType: ValidateFuelType = ValidateFuelType(),
    private val validateSourceDescription: ValidateSourceDescription = ValidateSourceDescription(),
    private val validateTrashContent: ValidateTrashContent = ValidateTrashContent(),
    private val validateAddress: ValidateAddress = ValidateAddress(),
    private val validateEvents: ValidateEvents = ValidateEvents(),
    private val validateTerritoryDescription:  ValidateTerritoryDescription = ValidateTerritoryDescription(),
    private val validateCountOfStumps: ValidateCountOfStumps = ValidateCountOfStumps(),
    private val validateDiameter: ValidateDiameter = ValidateDiameter(),
    private val validateSquare: ValidateSquare = ValidateSquare(),
    private val validateTypeOfMiniral: ValidateTypeOfMiniral = ValidateTypeOfMiniral(),
    private val validateVolume: ValidateVolume = ValidateVolume(),
    private val validateWoodType: ValidateWoodType = ValidateWoodType(),
    private val validateDescription: ValidateDescription = ValidateDescription(),
    @ApplicationContext private val context: Context
) : ViewModel() {
    var state by mutableStateOf(CreateIncidentReportState())
        private set

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: CreateIncidentReportEvent) {
        when (event) {
            is CreateIncidentReportEvent.DateChanged -> {
                when(event.dateType) {
                    0 -> {
                        state = state.copy(manualDate = event.date)
                    }
                    1 -> {
                        state = state.copy(startOfInspection = event.date)
                    }
                    2 -> {
                        state = state.copy(endOfInspection = event.date)
                    }
                }
            }

            is CreateIncidentReportEvent.IsSelectedIncidentKindChanged -> {
                state = state.copy(kind = event.incidentReportKind,
                                    selectedText = event.incidentReportKind.title)
                onGetAttributesItems()
            }

            is CreateIncidentReportEvent.TerritoryDescriprionChanged -> {
                state = state.copy(territoryDescription = event.description)
            }

            is CreateIncidentReportEvent.СadastralТumberChanged -> {
                state = state.copy(cadastralNumber = event.cadastralNumber)
            }

            is CreateIncidentReportEvent.IsBarrierChanged -> {
                state = state.copy(isBarrier = event.isBarrier)
            }

            is CreateIncidentReportEvent.TrashContentChanged -> {
                state = state.copy(trashContent = event.source_of_pollution)
            }

            is CreateIncidentReportEvent.SourceDescriptionChanged -> {
                state = state.copy(sourceDescription = event.description)
            }

            is CreateIncidentReportEvent.FuelTypeChanged -> {
                state = state.copy(fuelType = event.fuelType)
            }

            is CreateIncidentReportEvent.EventsChanged -> {
                state = state.copy(events = event.events)
            }

            CreateIncidentReportEvent.CreateIncidentReport -> {
                onCreateIncidentReport()
            }

            is CreateIncidentReportEvent.AddressChanged -> {
                state = state.copy(address = event.address)
            }

            is CreateIncidentReportEvent.GetLocation -> {
                state = state.copy(
                    lat = event.lat,
                    lng = event.lng,
                    address = getCompleteAddressString(event.lat, event.lng, event.context)
                )
            }

            is CreateIncidentReportEvent.OnMapClick -> {
                if(state.coordsPoligonIsVisible) {
                    if (state.points.count() < state.countOfLatElements) {
                        state = state.copy(
                            points = state.points + event.latLng,
                        )

                        val centerCoords = calculatePolygonCenter(polygonPoints = state.points)
                        val area = calculateRectangleArea(state.points)
                        val address = getCompleteAddressString(state.lat!!, state.lng!!, event.context)

                        state = state.copy(
                            address = address,
                            lat = centerCoords.latitude,
                            lng = centerCoords.longitude,
                            square = String.format("%.2f", area)
                        )
                    }
                } else {
                    val  address = getCompleteAddressString(event.latLng.latitude, event.latLng.longitude, event.context)

                    state = state.copy(
                        lat = event.latLng.latitude,
                        lng = event.latLng.longitude,
                        address = address
                    )
                }
            }

            is CreateIncidentReportEvent.GetIncident -> {
                if(event.id != null) {
                    onGetIncident(event.id)
                }
                else {
                    state = state.copy(
                        incidentId = null,
                        selectedIncident = null,
                    )
                }
            }

            CreateIncidentReportEvent.GetAttributesItems -> {
                onGetAttributesItems()
            }

            is CreateIncidentReportEvent.OnMarkerClick -> {
                state = state.copy(points = state.points.filterNot { it == event.latLng })
            }

            CreateIncidentReportEvent.GetIncidentList -> {
                getIncidentPagination()
            }

            is CreateIncidentReportEvent.CountOfStumpsChanged -> {
                state = state.copy(countOfStumps = event.events)
            }
            is CreateIncidentReportEvent.DiameterChanged -> {
                state = state.copy(diameter = event.diameter)
            }
            is CreateIncidentReportEvent.DrivewaysChanged -> {
                state = state.copy(driveways = event.driveways)
            }
            is CreateIncidentReportEvent.SquareChanged -> {
                state = state.copy(square = event.square.replace(",", "."))
            }
            is CreateIncidentReportEvent.TypeOfMiniralChanged -> {
                state = state.copy(typeOfMiniral = event.typeOfMiniral)
            }
            is CreateIncidentReportEvent.VolumeChanged -> {
                state = state.copy(volume = event.volume.replace(",", "."))
            }
            is CreateIncidentReportEvent.WoodTypeChanged -> {
                state = state.copy(woodType = event.woodType)
            }

            is CreateIncidentReportEvent.DescriptionChanged -> {
                state = state.copy(description = event.description)
            }

            is CreateIncidentReportEvent.TimeChanged -> {
                when(event.dateType) {
                    0 -> {
                        val dateTime = state.manualDate.withHour(event.hour).withMinute(event.minute)
                        state = state.copy(manualDate = dateTime)
                    }
                    1 -> {
                        val dateTime = state.startOfInspection.withHour(event.hour).withMinute(event.minute)
                        state = state.copy(startOfInspection = dateTime)
                    }
                    2 -> {
                        val dateTime = state.endOfInspection.withHour(event.hour).withMinute(event.minute)
                        state = state.copy(endOfInspection = dateTime)
                    }
                }
            }
        }
    }

    private fun onGetIncident(id: String) {
        val token = prefs.getString("inspector-jwt-token", null)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        if (token != null) {
            viewModelScope.launch {
                when (val response = incidentRepository.getIncident(id, token)) {
                    is Resource.Success -> {
                        if (response.data != null) {
                            state = state.copy(
                                kind = IncidentReportKind.getByIndex(response.data.kind.index)!!,
                                incidentId = response.data.id,
                                address = response.data.address,
                                lat = response.data.lat ?: 56.02,
                                lng = response.data.lng ?: 92.86,
                                files = response.data.incidentAttachments.map { attachment ->
                                    CreateIncidentReportAttachment(
                                        file = mutableStateOf((Constants.BASE_URL + "storage/download/" + attachment.savedFile.path).toUri()),
                                        description = mutableStateOf(attachment.description),
                                        manualDate = mutableStateOf(
                                            formatter.format(
                                                Instant.ofEpochMilli(
                                                    attachment.savedFile.createdAt.time
                                                ).atZone(ZoneId.systemDefault())
                                            )
                                        )
                                    )
                                }.toMutableStateList(),
                                selectedText = IncidentReportKind.getByIndex(response.data.kind.index)!!.title,
                                selectedIncident = response.data.toIncident()
                            )
                        }

                        validationEventChannel.send(ValidationEvent.Success)
                    }


                    is Resource.Error -> {
                        state = state.copy(
                            error = response.message!!,
                        )
                    }
                }
            }
        }
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

    private fun onCreateIncidentReport() {
        val validateCadastralNumberResult = validateCadastralNumber.execute(state.cadastralNumber, state.cadastralNumberIsRequired)
        val validateFuelTypeResult = validateFuelType.execute(state.fuelType, state.fuelTypeIsRequired)
        val validateSourceDescriptionResult = validateSourceDescription.execute(state.sourceDescription, state.sourceDescriptionIsRequired)
        val validateSourceOfPollutionResult = validateTrashContent.execute(state.trashContent, state.sourceOfPollutionIsRequired)
        val validateAddressResult = validateAddress.execute(state.address)
        val validateEventsResult = validateEvents.execute(state.events, state.eventsIsRequired)
        val validateTerritoryDescriptionResult = validateTerritoryDescription.execute(state.territoryDescription, state.territoryDescriptionIsRequired)
        val validateCountOfStumpsResult = validateCountOfStumps.execute(if(state.countOfStumps.isBlank()) 0 else state.countOfStumps.toInt(), state.countOfStumpsIsRequired)
        val validateDiameterResult = validateDiameter.execute(if(state.diameter.isBlank()) 0.0 else state.diameter.toDouble(), state.diameterIsRequired)
        val validateSquareResult = validateSquare.execute(if(state.square.isBlank()) 0.0 else state.square.toDouble(), state.squareIsRequired)
        val validateTypeOfMiniralResult = validateTypeOfMiniral.execute(state.typeOfMiniral, state.typeOfMineralIsRequired)
        val validateVolumeResult = validateVolume.execute(if(state.volume.isBlank()) 0.0 else state.volume.toDouble(), state.volumeIsRequired)
        val validateWoodTypeResult = validateWoodType.execute(state.woodType, state.woodTypeIsRequired)
        val validateDescriptionResult = validateDescription.execute(state.description)

        val hasError = listOf(
            validateCadastralNumberResult,
            validateFuelTypeResult,
            validateSourceDescriptionResult,
            validateSourceOfPollutionResult,
            validateAddressResult,
            validateEventsResult,
            validateTerritoryDescriptionResult,
            validateCountOfStumpsResult,
            validateDiameterResult,
            validateSquareResult,
            validateTypeOfMiniralResult,
            validateVolumeResult,
            validateWoodTypeResult,
            validateDescriptionResult
        ).any { !it.isSuccessful }

        if (hasError || state.countOfLatElements != state.points.count() || state.lat == null || state.lng == null) {
            state = state.copy(
                cadastralNumberError = validateCadastralNumberResult.errorMessage,
                fuelTypeError = validateFuelTypeResult.errorMessage,
                sourceDescriptionError = validateSourceDescriptionResult.errorMessage,
                sourceOfPollutionError = validateSourceOfPollutionResult.errorMessage,
                addressError = validateAddressResult.errorMessage,
                eventsError = validateEventsResult.errorMessage,
                territoryDescriptionError = validateTerritoryDescriptionResult.errorMessage,
                coordsError = if(state.countOfLatElements != state.points.count()) "Должно быть ${state.countOfLatElements} точек у области"
                                        else if(state.lat == null || state.lng == null) "Укажите координаты акта"
                                            else null,
                countOfStumpsError = validateCountOfStumpsResult.errorMessage,
                diameterError =  validateDiameterResult.errorMessage,
                squareError =  validateSquareResult.errorMessage,
                typeOfMiniralError =  validateTypeOfMiniralResult.errorMessage,
                volumeError =  validateVolumeResult.errorMessage,
                woodTypeError =  validateWoodTypeResult.errorMessage,
                descriptionError = validateDescriptionResult.errorMessage
            )

            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(
                isLoading = true,
                error = null
            )

            val token = prefs.getString("inspector-jwt-token", null)

            if (token != null) {

                val manualDate = state.manualDate.atOffset(ZoneOffset.UTC)
                val startOfInspection = state.startOfInspection.atOffset(ZoneOffset.UTC)
                val endOfInspection = state.endOfInspection.atOffset(ZoneOffset.UTC)
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

                val incidentReport = CreateIncidentReport(
                    attributesVersion = 1,
                    manualDate = outputFormatter.format(manualDate),
                    startOfInspection = outputFormatter.format(startOfInspection),
                    endOfInspection = outputFormatter.format(endOfInspection),
                    address = state.address,
                    locationId = state.locationId,
                    lat = state.lat!!,
                    lng = state.lng!!,
                    kind = state.kind.index,
                    incidentId = state.incidentId,
                    description = state.description
                )

                when (val response = incidentReportRepository.createIncidentReport(
                    incidentReport.toCreateIncidentReportDto(),
                    token
                )) {
                    is Resource.Success -> {

                        if (response.data != null) {
                            state = state.copy(incidentReportId = response.data.id)

                            for (file in state.files) {
                                val fileImg: File =
                                    uriToFile(file.file.value) ?: downloadImageFromUrl(file.file.value.toString(), file.file.value.toString().substringAfterLast('/'))


                                val filePart: MultipartBody.Part =
                                    MultipartBody.Part.createFormData(
                                        "file",
                                        fileImg.getName().replace("%2F", ""),
                                        RequestBody.create("image/*".toMediaType(), fileImg)
                                    )

                                val descriptionPart: RequestBody =
                                    RequestBody.create(
                                        "text/plain".toMediaType(),
                                        file.description.value
                                    )
                                val manualFileDatePart: RequestBody =
                                    RequestBody.create(
                                        "text/plain".toMediaType(),
                                        file.manualDate.value
                                    )

                                incidentReportRepository.attachFileToIncidentReport(
                                    filePart,
                                    descriptionPart,
                                    manualFileDatePart,
                                    response.data.id,
                                    token
                                )
                            }

                            val updateAttributesList = mutableListOf<UpdateIncidentReportAttribute>()
                            if (state.cadastralNumberIsVisible && state.cadastralNumber.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("CADASTRAL_NUMBER", state.cadastralNumber, null, null))
                            }
                            if (state.isBarrierIsVisible) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("BARRIER", null, null, state.isBarrier))
                            }
                            if (state.drivewaysIsVisible) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("DRIVEWAYS", null, null, state.driveways))
                            }
                            if (state.trashContentIsVisible && state.trashContent.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("TRASH_CONTENT", state.trashContent, null, null))
                            }
                            if (state.sourceDescriptionIsVisible && state.sourceDescription.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("SOURCE_DESCRIPTION", state.sourceDescription, null, null))
                            }
                            if (state.fuelTypeIsVisible && state.fuelType.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("FUEL_TYPE", state.fuelType, null, null))
                            }
                            if (state.eventsIsVisible && state.events.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("EVENTS", state.events, null, null))
                            }
                            if (state.territoryDescriptionIsVisible && state.territoryDescription.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("TERRITORY_DESCRIPTION", state.territoryDescription, null, null))
                            }
                            if (state.squareIsVisible && state.square.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("SQUARE", null, state.square.toDouble(), null))
                            }
                            if (state.volumeIsVisible && state.volume.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("VOLUME", null, state.volume.toDouble(), null))
                            }
                            if (state.diameterIsVisible && state.diameter.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("DIAMETER", null, state.diameter.toDouble(), null))
                            }
                            if (state.countOfStumpsIsVisible && state.countOfStumps.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("COUNT_OF_STUMPS", null, state.countOfStumps.toDouble(), null))
                            }
                            if (state.typeOfMiniralIsVisible && state.typeOfMiniral.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("TYPE_OF_MINIRAl", state.typeOfMiniral, null, null))
                            }
                            if (state.woodTypeIsVisible && state.woodType.isNotEmpty()) {
                                updateAttributesList.add(UpdateIncidentReportAttribute("WOOD_TYPE", state.woodType, null, null))
                            }
                            if(state.coordsPoligonIsVisible) {
                                var index = 1
                                state.points.forEach {
                                    updateAttributesList.add(UpdateIncidentReportAttribute("GEO_${index}_LAT", null, it.latitude, null))
                                    updateAttributesList.add(UpdateIncidentReportAttribute("GEO_${index}_LNG", null, it.longitude, null))
                                    index++
                                }
                            }

                            val mutableUpdateAttributesList: MutableList<UpdateIncidentReportAttributeDto> = updateAttributesList.map { it.toUpdateIncidentReportAttributeDto() }.toMutableList()

                            incidentReportRepository.updateIncidentReportAttributes(mutableUpdateAttributesList, response.data.id, token)
                        }

                        state = state.copy(
                            isLoading = false,
                            error = null,
                            isIncidentReportCreated = true
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

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun downloadImageFromUrl (uri: String, fileName: String): File {
        return withContext(Dispatchers.IO) {
            val url = URL(uri)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val file = File(context.filesDir, fileName)

            val outputStream = FileOutputStream(file)
            connection.inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return@withContext file
        }
    }

    private fun onGetAttributesItems() {
        val token = prefs.getString("inspector-jwt-token", null)
        if (token != null) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val response = incidentReportRepository.getIncidentReportAttributeItem(
                    state.kind,
                    state.attributesVersion,
                    token
                )) {
                    is Resource.Success -> {
                        if (response.data != null) {
                            val cadastralVisible = response.data.any { it.name == "CADASTRAL_NUMBER" }
                            val barrierVisible = response.data.any { it.name == "BARRIER" }
                            val pollutionVisible = response.data.any { it.name == "TRASH_CONTENT" }
                            val sourceDescriptionVisible = response.data.any { it.name == "SOURCE_DESCRIPTION" }
                            val fuelTypeVisible = response.data.any { it.name == "FUEL_TYPE" }
                            val eventsVisible = response.data.any { it.name == "EVENTS" }
                            val territoryDescriptionVisible = response.data.any { it.name == "TERRITORY_DESCRIPTION" }
                            val drivewaysVisible = response.data.any { it.name == "DRIVEWAYS" }
                            val squareVisible = response.data.any { it.name == "SQUARE" }
                            val volumeVisible = response.data.any { it.name == "VOLUME" }
                            val typeOfMineralVisible = response.data.any { it.name == "TYPE_OF_MINIRAl" }
                            val woodTypeVisible = response.data.any { it.name == "WOOD_TYPE" }
                            val countOfStumpsVisible = response.data.any { it.name == "COUNT_OF_STUMPS" }
                            val diameterVisible = response.data.any { it.name == "DIAMETER" }

                            val countOfLatElements = response.data.count { it.name.contains("LAT") }
                            val countOfLngElements = response.data.count { it.name.contains("LNG") }

                            val coordsVisible = countOfLngElements > 0 && countOfLatElements > 0

                            val cadastralRequired = response.data.firstOrNull { it.name == "CADASTRAL_NUMBER" }?.isRequired ?: false
                            val barrierRequired = response.data.firstOrNull { it.name == "BARRIER" }?.isRequired ?: false
                            val pollutionRequired = response.data.firstOrNull { it.name == "TRASH_CONTENT" }?.isRequired ?: false
                            val sourceDescriptionRequired = response.data.firstOrNull { it.name == "SOURCE_DESCRIPTION" }?.isRequired ?: false
                            val fuelTypeRequired = response.data.firstOrNull { it.name == "FUEL_TYPE" }?.isRequired ?: false
                            val eventsRequired = response.data.firstOrNull { it.name == "EVENTS" }?.isRequired ?: false
                            val territoryDescriptionRequired = response.data.firstOrNull { it.name == "TERRITORY_DESCRIPTION" }?.isRequired ?: false
                            val drivewaysRequired = response.data.firstOrNull { it.name == "DRIVEWAYS" }?.isRequired ?: false
                            val squareRequired = response.data.firstOrNull { it.name == "SQUARE" }?.isRequired ?: false
                            val volumeRequired = response.data.firstOrNull { it.name == "VOLUME" }?.isRequired ?: false
                            val typeOfMineralRequired = response.data.firstOrNull { it.name == "TYPE_OF_MINIRAl" }?.isRequired ?: false
                            val woodTypeRequired = response.data.firstOrNull { it.name == "WOOD_TYPE" }?.isRequired ?: false
                            val countOfStumpsRequired = response.data.firstOrNull { it.name == "COUNT_OF_STUMPS" }?.isRequired ?: false
                            val diameterRequired = response.data.firstOrNull { it.name == "DIAMETER" }?.isRequired ?: false

                            state = state.copy(
                                cadastralNumberIsVisible = cadastralVisible,
                                isBarrierIsVisible = barrierVisible,
                                trashContentIsVisible = pollutionVisible,
                                sourceDescriptionIsVisible = sourceDescriptionVisible,
                                fuelTypeIsVisible = fuelTypeVisible,
                                coordsPoligonIsVisible = coordsVisible,
                                eventsIsVisible = eventsVisible,
                                territoryDescriptionIsVisible = territoryDescriptionVisible,
                                drivewaysIsVisible = drivewaysVisible,
                                squareIsVisible = squareVisible,
                                volumeIsVisible = volumeVisible,
                                typeOfMiniralIsVisible = typeOfMineralVisible,
                                woodTypeIsVisible = woodTypeVisible,
                                countOfStumpsIsVisible = countOfStumpsVisible,
                                diameterIsVisible = diameterVisible,

                                cadastralNumberIsRequired = cadastralRequired,
                                isBarrierIsRequired = barrierRequired,
                                sourceOfPollutionIsRequired = pollutionRequired,
                                sourceDescriptionIsRequired = sourceDescriptionRequired,
                                fuelTypeIsRequired = fuelTypeRequired,
                                eventsIsRequired = eventsRequired,
                                territoryDescriptionIsRequired = territoryDescriptionRequired,
                                drivewaysIsRequired = drivewaysRequired,
                                squareIsRequired = squareRequired,
                                volumeIsRequired = volumeRequired,
                                typeOfMineralIsRequired = typeOfMineralRequired,
                                woodTypeIsRequired = woodTypeRequired,
                                countOfStumpsIsRequired = countOfStumpsRequired,
                                diameterIsRequired = diameterRequired,

                                countOfLatElements = countOfLatElements,
                                countOfLngElements = countOfLngElements
                            )
                        }

                        validationEventChannel.send(ValidationEvent.Success)
                    }


                    is Resource.Error -> {
                        state = state.copy(
                            error = response.message!!,
                        )
                    }
                }
            }
        }
    }

    private fun getIncidentPagination() {
        state = state.copy(incidents = Pager(PagingConfig(pageSize = 5)) {
            IncidentListPagination(incidentRepository,
                prefs,
                null,
                null,
                1,
                false)
        }.flow)
    }

    private fun calculatePolygonCenter(polygonPoints: List<LatLng>): LatLng {
        var latSum = 0.0
        var lngSum = 0.0

        for (point in polygonPoints) {
            latSum += point.latitude
            lngSum += point.longitude
        }

        val latCenter = latSum / polygonPoints.size
        val lngCenter = lngSum / polygonPoints.size

        return LatLng(latCenter, lngCenter)
    }

    fun calculateRectangleArea(points: List<LatLng>): Double {
        if(points.count() == 4) {
            val area = SphericalUtil.computeArea(points)
            return area
        }
        return 0.0
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}