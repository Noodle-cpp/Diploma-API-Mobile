package com.example.greensignal.presentation.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.RequiresPermission
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.domain.model.response.Incident
import com.example.greensignal.presentation.event.CreateIncidentReportEvent
import com.example.greensignal.presentation.state.CreateIncidentReportState
import com.example.greensignal.presentation.ui.dialog.PermissionDialog
import com.example.greensignal.presentation.ui.element.AddPhotoIncidentReportElement
import com.example.greensignal.presentation.ui.element.AddedIncidentReportAttachmentElement
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar


@Composable
fun CreateIncidentReportScreen(incidentId: String?,
                               navController: NavController,
                               state: CreateIncidentReportState,
                               onEvent: (CreateIncidentReportEvent) -> Unit) {

    LaunchedEffect(key1 = true) {
        onEvent(CreateIncidentReportEvent.GetAttributesItems)
    }

    LaunchedEffect(state.isIncidentReportCreated) {
        if(state.incidentReportId != null) {
            navController.navigate(Screen.IncidentReportScreen.withArgs(state.incidentReportId)) {
                popUpTo(Screen.PersonalAccountScreen.route) { inclusive = true }
            }
        }
    }

    if(state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            HeaderRowBackElement(
                navController,
                Screen.PersonalAccountScreen.route,
                stringResource(id = R.string.my_inspector_profile_title)
            )

            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CurrentLocationScreen(navController, state, onEvent, incidentId)
        }
    }
}


@SuppressLint("MissingPermission")
@Composable
fun CurrentLocationScreen(navController: NavController,
                          state: CreateIncidentReportState,
                          onEvent: (CreateIncidentReportEvent) -> Unit,
                          incidentId: String?) {
    val permissions = listOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )

    Spacer(modifier = Modifier.height(15.dp))

    PermissionDialog(
        permissions = permissions,
        description = "Чтобы использовать приложение, пожалуйста, дайте доступ к вашему местоположению",
        requiredPermissions = listOf(permissions.first()),
        navController = navController,
        onGranted = {
            IncidentReport(
                navController,
                state,
                onEvent,
                incidentId,
                usePreciseLocation = it.contains(Manifest.permission.ACCESS_FINE_LOCATION),
            )
        },
    )
}

@OptIn(ExperimentalComposeUiApi::class, MapsComposeExperimentalApi::class)
@RequiresPermission(
    anyOf = [android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
private fun IncidentReport(navController: NavController,
                           state: CreateIncidentReportState,
                           onEvent: (CreateIncidentReportEvent) -> Unit,
                           incidentId: String?,
                           usePreciseLocation: Boolean) {
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            val priority = if (usePreciseLocation) {
                Priority.PRIORITY_HIGH_ACCURACY
            } else {
                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            }
            if (incidentId.isNullOrEmpty()) {
                val result = locationClient.getCurrentLocation(
                    priority,
                    CancellationTokenSource().token,
                ).await()
                result?.let { fetchedLocation ->
                    onEvent(
                        CreateIncidentReportEvent.GetLocation(
                            fetchedLocation.latitude,
                            fetchedLocation.longitude, context
                        )
                    )
                }
            }
            else {
                onEvent(CreateIncidentReportEvent.GetIncident(incidentId))
            }
        }
    }

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
    ) {

        HeaderRowBackElement(
            navController,
            if(!incidentId.isNullOrEmpty()) Screen.IncidentScreen.withArgs(incidentId) else Screen.IncidentReportListScreen.route,
            if(!incidentId.isNullOrEmpty()) stringResource(id = R.string.incident) else stringResource(id = R.string.my_acts)
        )

        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = stringResource(id = R.string.createAct),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(10.dp))

            ExpandableItem(onEvent, state, state.incidents)

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                KindDropDownMenu(onEvent, state)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = {
                    onEvent(CreateIncidentReportEvent.DescriptionChanged(it))
                },
                label = { Text(stringResource(id = R.string.description)) },
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
                isError = state.descriptionError != null
            )
            state.descriptionError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            DateTimePicker(onEvent, state.manualDate, 0, stringResource(id = R.string.date))

            Spacer(modifier = Modifier.height(15.dp))

            if(state.lat != null && state.lng != null) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),

                    onMapClick = {
                        onEvent(CreateIncidentReportEvent.OnMapClick(it, context))
                    }
                ) {
                    MapEffect(key1 = state.incidentId) {
                            it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(state.lat!!, state.lng!!), 15f))
                    }
                    MapEffect(state.kind) {
                        onEvent(CreateIncidentReportEvent.IsSelectedIncidentKindChanged(state.kind))
                    }

                    if (state.coordsPoligonIsVisible) {
                        state.points.forEach { latLng ->
                            Marker(
                                state = MarkerState(position = latLng),
                                icon = createDotMarker(
                                    MaterialTheme.colorScheme.outline,
                                    25f
                                ),//BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                                draggable = false,
                                zIndex = 1f,
                                onClick = {
                                    onEvent(CreateIncidentReportEvent.OnMarkerClick(latLng))
                                    false
                                }
                            )
                        }

                        if (state.points.isNotEmpty()) {
                            Polygon(
                                points = state.points,
                                fillColor = Color.Transparent,
                                strokeColor = MaterialTheme.colorScheme.outline,
                            )
                        }
                    } else {
                        Marker(
                            state = MarkerState(position = LatLng(state.lat, state.lng)),
                            icon = BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        )
                    }
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(10.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            state.coordsError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = state.address,
                onValueChange = {
                    onEvent(CreateIncidentReportEvent.AddressChanged(it))
                },
                label = { Text(stringResource(id = R.string.address)) },
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
                isError = state.addressError != null
            )
            state.addressError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            DateTimePicker(onEvent, state.startOfInspection, 1, stringResource(id = R.string.startOfInspection))

            Spacer(modifier = Modifier.height(10.dp))

            DateTimePicker(onEvent, state.endOfInspection, 2, stringResource(id = R.string.endOfInspection))

            Spacer(modifier = Modifier.height(10.dp))

            if(state.territoryDescriptionIsVisible) {
                OutlinedTextField(
                    value = state.territoryDescription,
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.TerritoryDescriprionChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.territory_description)) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
                state.territoryDescriptionError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if(state.cadastralNumberIsVisible) {
                OutlinedTextField(
                    value = state.cadastralNumber,
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.СadastralТumberChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.cadastral_number)) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.cadastralNumberError != null
                )
                state.cadastralNumberError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if(state.isBarrierIsVisible) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            onEvent(CreateIncidentReportEvent.IsBarrierChanged(!state.isBarrier))
                        })) {
                    Checkbox(
                        checked = state.isBarrier,
                        onCheckedChange = { onEvent(CreateIncidentReportEvent.IsBarrierChanged(it)) }
                    )
                    Text(
                        text = stringResource(id = R.string.is_barrier),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if(state.trashContentIsVisible) {
                OutlinedTextField(
                    value = state.trashContent,
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.TrashContentChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.trash_content)) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.sourceOfPollutionError != null
                )
                state.sourceOfPollutionError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if(state.sourceDescriptionIsVisible) {
                OutlinedTextField(
                    value = state.sourceDescription,
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.SourceDescriptionChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.source_description)) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.sourceDescriptionError != null
                )
                state.sourceDescriptionError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if(state.fuelTypeIsVisible) {
                OutlinedTextField(
                    value = state.fuelType,
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.FuelTypeChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.fuel_type)) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.fuelTypeError != null
                )
                state.fuelTypeError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if(state.drivewaysIsVisible) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            onEvent(CreateIncidentReportEvent.DrivewaysChanged(!state.driveways))
                        })) {
                    Checkbox(
                        checked = state.driveways,
                        onCheckedChange = { onEvent(CreateIncidentReportEvent.DrivewaysChanged(it)) }
                    )
                    Text(
                        text = stringResource(id = R.string.driveways),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if(state.eventsIsVisible) {
                OutlinedTextField(
                    value = state.events,
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.EventsChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.events)) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
                state.eventsError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
            }

            if(state.countOfStumpsIsVisible) {
                OutlinedTextField(
                    value = state.countOfStumps.toString(),
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.CountOfStumpsChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.count_of_stumps)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                state.countOfStumpsError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
            }

            if(state.diameterIsVisible) {
                OutlinedTextField(
                    value = state.diameter.toString(),
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.DiameterChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.diameter)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                state.diameterError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
            }

            if(state.squareIsVisible) {
                OutlinedTextField(
                    value = state.square.toString(),
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.SquareChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.square)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                state.squareError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
            }

            if(state.typeOfMiniralIsVisible) {
                OutlinedTextField(
                    value = state.typeOfMiniral,
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.TypeOfMiniralChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.type_of_miniral)) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
                state.typeOfMiniralError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
            }

            if(state.volumeIsVisible) {
                OutlinedTextField(
                    value = state.volume.toString(),
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.VolumeChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.volume)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                state.volumeError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
            }

            if(state.woodTypeIsVisible) {
                OutlinedTextField(
                    value = state.woodType,
                    onValueChange = {
                        onEvent(CreateIncidentReportEvent.WoodTypeChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.wood_type)) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
                state.woodTypeError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
            }

            FilesColumn(state)

            Spacer(modifier = Modifier.height(30.dp))

            AddPhotoIncidentReportElement(state.files)

            Spacer(modifier = Modifier.height(35.dp))

            Button(
                onClick = {
                    onEvent(CreateIncidentReportEvent.CreateIncidentReport)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = stringResource(id = R.string.create_incident_report),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
private fun FilesColumn(state: CreateIncidentReportState) {
    Box {
        val columnHeight = state.files.size * 125

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(columnHeight.dp),
        ) {
            itemsIndexed(state.files) { index, file ->
                AddedIncidentReportAttachmentElement(
                    description = file.description.value,
                    url = file.file.value,
                    date = file.manualDate.value,
                    index,
                    state.files.map { it }.toMutableList(),
                    onDeleteAction = {}
                )
            }
        }
    }
}

@Composable
fun ExpandableItem(onEvent: (CreateIncidentReportEvent) -> Unit, state: CreateIncidentReportState, items: Flow<PagingData<Incident>>) {
    var expanded by remember { mutableStateOf(false) }

    val incidents: LazyPagingItems<Incident> = items.collectAsLazyPagingItems()

    val transition = updateTransition(targetState = expanded)
    val height by transition.animateDp { if (it)  500.dp else 120.dp }
    val angle: Float by transition.animateFloat { if (it) 0F else -90F }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable {
                if (!expanded) onEvent(CreateIncidentReportEvent.GetIncidentList)
                expanded = !expanded
            }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(text = "Сообщение о нарушении", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = if (state.selectedIncident != null) "${state.selectedIncident.kind.title} - ${state.selectedIncident.address}"
                    else stringResource(
                        id = R.string.attach_incident
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "expand",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .rotate(angle)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (expanded) {
            Column {
                Column(modifier = Modifier.clickable(onClick = {
                    onEvent(CreateIncidentReportEvent.GetIncident(null))
                    expanded = !expanded
                })) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = Color.LightGray
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.without_incident),
                                style = MaterialTheme.typography.titleMedium,
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = stringResource(id = R.string.select_without_incident),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = Color.LightGray
                    )
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(incidents.itemCount) {index ->
                        Column(modifier = Modifier.clickable(onClick = {
                            onEvent(CreateIncidentReportEvent.GetIncident(incidents[index]!!.id))
                            expanded = !expanded
                        })) {

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column {

                                    Text(
                                        text = incidents[index]!!.kind.title,
                                        style = MaterialTheme.typography.titleMedium,
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = incidents[index]!!.address,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 2.dp,
                                color = Color.LightGray
                            )
                        }
                    }
                    incidents.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier.fillParentMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(10.dp),
                                            color = MaterialTheme.colorScheme.secondary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            }
                            loadState.refresh is LoadState.Error -> {
                                val errorState = incidents.loadState.refresh as LoadState.Error
                                item {
                                    //ErrorItem(errorState.error.localizedMessage)
                                }
                            }
                            loadState.append is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(10.dp),
                                            color = MaterialTheme.colorScheme.secondary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            }
                            loadState.append is LoadState.Error -> {
                                val errorState = incidents.loadState.append as LoadState.Error
                                item {
                                    //ErrorItem(errorState.error.localizedMessage)
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = Color.LightGray
        )
    }
}

@Composable
private fun createDotMarker(color: Color, radius: Float): BitmapDescriptor {
    val dotBitmap = Bitmap.createBitmap((radius * 2).toInt(), (radius * 2).toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(dotBitmap)
    val paint = Paint().apply {
        this.color = color.toArgb()
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    canvas.drawCircle(radius, radius, radius, paint)

    return BitmapDescriptorFactory.fromBitmap(dotBitmap)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KindDropDownMenu(onEvent: (CreateIncidentReportEvent) -> Unit, state: CreateIncidentReportState) {
    val incidentReportKinds = IncidentReportKind.values()
    var expanded by remember { mutableStateOf(false) }
//    var selectedText by remember { mutableStateOf(IncidentReportKind.values()[0].title) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.incident_kind),
            style = MaterialTheme.typography.labelLarge
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = state.selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                incidentReportKinds.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.title) },
                        onClick = {
//                            selectedText = item.title
                            expanded = false
                            onEvent(CreateIncidentReportEvent.IsSelectedIncidentKindChanged(item))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePicker(onEvent: (CreateIncidentReportEvent) -> Unit,
                           date: LocalDateTime, dateType: Int, title: String) {
    val isOpen = remember { mutableStateOf(false)}
    val isOpenTimePicker = remember { mutableStateOf(false)}

    Row(modifier = Modifier
        .fillMaxWidth()) {
        OutlinedTextField(
            readOnly = true,
            value = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            label = { Text(title) },
            onValueChange = {
            },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                isOpen.value = true
                            }
                        }
                    }
                }
        )

        Spacer(modifier = Modifier.width(10.dp))

        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = {
                isOpen.value = true
            }
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                imageVector = Icons.Default.DateRange,
                contentDescription = "Calendar")
        }
    }

    if(isOpenTimePicker.value) {
        Dialog(onDismissRequest = {
        }) {
            Column(modifier = Modifier.background(Color.White).padding(20.dp)) {
                val timeState = rememberTimePickerState(11, 30, true)
                TimePicker(state = timeState)
                Row(modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()) {
                    TextButton(
                        onClick = {
                            isOpenTimePicker.value = false
                        }
                    ) { Text("Отмена") }
                    TextButton(
                        onClick = {
                            onEvent(CreateIncidentReportEvent.TimeChanged(timeState.hour, timeState.minute, dateType))
                            isOpenTimePicker.value = false
                        }
                    ) { Text("OK") }
                }
            }
        }
    }

    if (isOpen.value) {
        val calendar = Calendar.getInstance()
        calendar.set(LocalDate.now().year, LocalDate.now().month.value, LocalDate.now().dayOfMonth)
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)

        DatePickerDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = {
                    onEvent(
                        CreateIncidentReportEvent.DateChanged(
                            LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(datePickerState.selectedDateMillis!!), ZoneId.systemDefault()), dateType))
                    isOpen.value = false
                    isOpenTimePicker.value = true
                }) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(id = R.string.apply)
                    )
                }
            },
            dismissButton = {
                Button(onClick = {
                    isOpen.value = false
                }) {
                    Text(stringResource(id = R.string.close))
                }
            }
        ) {
            DatePicker(state = datePickerState,
                title = {
                    Text(
                        text = stringResource(id = R.string.date),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(
                            PaddingValues(
                                start = 24.dp,
                                end = 12.dp,
                                top = 16.dp
                            )
                        )
                    )
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true,
    heightDp = 1300)
fun CreateActScreenPreview() {
    GreenSignalTheme {
        CreateIncidentReportScreen(incidentId = null, navController = rememberNavController(), state = CreateIncidentReportState()) {

        }
    }
}