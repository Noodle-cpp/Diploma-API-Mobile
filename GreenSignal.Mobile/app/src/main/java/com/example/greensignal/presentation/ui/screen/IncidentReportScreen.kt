package com.example.greensignal.presentation.ui.screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.common.Constants
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus
import com.example.greensignal.presentation.event.IncidentReportEvent
import com.example.greensignal.presentation.state.IncidentReportState
import com.example.greensignal.presentation.ui.dialog.ConfirmationDialog
import com.example.greensignal.presentation.ui.element.ArgumentFieldElement
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.element.IncidentReportAttachmentElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Composable
fun IncidentReportScreen(incidentReportId: String,
                         navController: NavController,
                         state: IncidentReportState,
                         onEvent: (IncidentReportEvent) -> Unit) {
    LaunchedEffect(key1 = true) {
        onEvent(IncidentReportEvent.GetIncidentReport(incidentReportId))
    }

    if(state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            HeaderRowBackElement(
                navController,
                Screen.IncidentReportListScreen.route,
                stringResource(id = R.string.my_incident_reports)
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
                .verticalScroll(rememberScrollState())
        ) {
            IncidentReport(incidentReportId, navController, state, onEvent)
        }
    }
}

@Composable
private fun IncidentReport(incidentReportId: String,
                           navController: NavController,
                           state: IncidentReportState,
                           onEvent: (IncidentReportEvent) -> Unit) {
    val context = LocalContext.current


    val showDialog =  remember { mutableStateOf(false) }

    if(showDialog.value) {
        ConfirmationDialog(
            show = showDialog.value,
            onDismiss = { showDialog.value = false },
            onConfirm = {
                onEvent(IncidentReportEvent.ArchiveIncidentReport)
                navController.navigate(Screen.IncidentReportListScreen.route) {
                    popUpTo(Screen.IncidentReportListScreen.route) { inclusive = true }
                }

                showDialog.value = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        HeaderRowBackElement(
            navController,
            Screen.IncidentReportListScreen.route,
            stringResource(id = R.string.my_incident_reports),
            additionalTitle = stringResource(id = R.string.edit),
            additionalRoute = Screen.UpdateIncidentReportScreen.withArgs(incidentReportId),
            additionalIcon = Icons.Default.Edit
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)) {

            Text(
                modifier = Modifier,
                fontSize = 20.sp,
                text = "Акт №${state.incidentReport.serialNumber}",
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(10.dp))

            ArgumentFieldElement(title = stringResource(id = R.string.incident_kind), argument = state.incidentReport.kind.title)
            ArgumentFieldElement(title = stringResource(id = R.string.description), argument = state.incidentReport.description)
            ArgumentFieldElement(title = stringResource(id = R.string.address), argument = state.incidentReport.address)
            ArgumentFieldElement(title = stringResource(id = R.string.date), argument = SimpleDateFormat("dd.MM.yyyy HH:mm").format(state.incidentReport.manualDate))

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(state.incidentReport.lat, state.incidentReport.lng), 15f)
                },
            ) {

                state.points.forEach { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        icon = createDotMarker(
                            MaterialTheme.colorScheme.outline,
                            25f
                        ),//BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                        draggable = false,
                        zIndex = 1f
                    )
                }

                if (state.points.isNotEmpty()) {
                    Polygon(
                        points = state.points,
                        fillColor = Color.Transparent,
                        strokeColor = MaterialTheme.colorScheme.outline,
                    )
                }
                if (state.points.count() == 0)
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                state.incidentReport.lat,
                                state.incidentReport.lng
                            )
                        ),
                        icon = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    )

            }

            ArgumentFieldElement(title = stringResource(id = R.string.coords), argument = "${state.incidentReport.lat}/${state.incidentReport.lng}")
            ArgumentFieldElement(title = stringResource(id = R.string.startOfInspection), argument = SimpleDateFormat("dd.MM.yyyy HH:mm").format(state.incidentReport.startOfInspection))
            ArgumentFieldElement(title = stringResource(id = R.string.endOfInspection), argument = SimpleDateFormat("dd.MM.yyyy HH:mm").format(state.incidentReport.endOfInspection))

            state.incidentReport.incidentReportAttributes.forEach {
                if(!it.name.contains("LAT") && !it.name.contains("LNG")) {
                    var attributeTitle = it.name
                    when (it.name) {
                        "CADASTRAL_NUMBER" -> {
                            attributeTitle = stringResource(id = R.string.cadastral_number)
                        }

                        "BARRIER" -> {
                            attributeTitle = stringResource(id = R.string.is_barrier)
                        }

                        "TRASH_CONTENT" -> {
                            attributeTitle = stringResource(id = R.string.trash_content)
                        }

                        "DRIVEWAYS" -> {
                            attributeTitle = stringResource(id = R.string.driveways)
                        }

                        "SOURCE_DESCRIPTION" -> {
                            attributeTitle = stringResource(id = R.string.source_description)
                        }

                        "FUEL_TYPE" -> {
                            attributeTitle = stringResource(id = R.string.fuel_type)
                        }

                        "EVENTS" -> {
                            attributeTitle = stringResource(id = R.string.events)
                        }

                        "TERRITORY_DESCRIPTION" -> {
                            attributeTitle = stringResource(id = R.string.territory_description)
                        }

                        "SQUARE" -> {
                            attributeTitle = stringResource(id = R.string.square)
                        }

                        "VOLUME" -> {
                            attributeTitle = stringResource(id = R.string.volume)
                        }

                        "DIAMETER" -> {
                            attributeTitle = stringResource(id = R.string.diameter)
                        }

                        "COUNT_OF_STUMPS" -> {
                            attributeTitle = stringResource(id = R.string.count_of_stumps)
                        }

                        "TYPE_OF_MINIRAl" -> {
                            attributeTitle = stringResource(id = R.string.type_of_miniral)
                        }

                        "WOOD_TYPE" -> {
                            attributeTitle = stringResource(id = R.string.wood_type)
                        }

                    }

                    if (it.stringValue != null)
                        ArgumentFieldElement(title = attributeTitle, argument = it.stringValue)
                    else if (it.boolValue != null)
                        ArgumentFieldElement(title = attributeTitle, argument = if(it.boolValue) "Есть" else "Нет")
                    else if (it.numberValue != null)
                        ArgumentFieldElement(title = attributeTitle, argument = it.numberValue.toString())
                }
            }

            val columnHeight = state.incidentReport.incidentReportAttachements.size * 130

            Spacer(modifier = Modifier.height(10.dp))

            if (state.incidentReport.incidentReportAttachements.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(columnHeight.dp),
                    userScrollEnabled = false
                ) {
                    items(state.incidentReport.incidentReportAttachements.size) { index ->
                        IncidentReportAttachmentElement(
                            description = state.incidentReport.incidentReportAttachements[index].description,
                            date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(state.incidentReport.incidentReportAttachements[index].manualDate),
                            url = Constants.BASE_URL + "storage/download/" + state.incidentReport.incidentReportAttachements[index].savedFile.path
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            } else {
                Text(
                    text = "Нет прикреплённых к акту фотографий",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(15.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                          navController.navigate(Screen.CreatePetitionScreen.route + "/incidentReport/${state.incidentReport.id}")
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = stringResource(id = R.string.create_petition),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(onClick = {

                    })) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(25.dp),
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Exit",
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(text = stringResource(id = R.string.download_pdf),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.clickable(onClick = {
                            onEvent(IncidentReportEvent.DownloadPdf(context))
                        }))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                   showDialog.value = true
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = stringResource(id = R.string.delete),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
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

@Composable
@Preview(showBackground = true,
            heightDp = 1000)
fun IncidentReportScreenPreview() {
    GreenSignalTheme {
        IncidentReportScreen(incidentReportId = "",
                                navController = rememberNavController(),
                                state = IncidentReportState(
                                    incidentReport = com.example.greensignal.domain.model.response.IncidentReport(
                                    serialNumber = "1-1",
                                    address = "Улица Пушкина дом Калатушкина",
                                    manualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
                                    startOfInspection = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
                                    endOfInspection = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
                                    status = IncidentReportStatus.Draft,
                                    kind = IncidentReportKind.Dump,
                                    lat = 92.56,
                                    lng = 56.92,
                                    )
                                )) {
        }
    }
}
