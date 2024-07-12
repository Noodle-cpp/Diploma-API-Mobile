package com.example.greensignal.presentation.ui.screen

import android.telephony.PhoneNumberUtils
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.common.Constants
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import com.example.greensignal.data.remote.dto.response.PetitionStatus
import com.example.greensignal.data.remote.dto.response.ReportType
import com.example.greensignal.domain.model.response.Citizen
import com.example.greensignal.domain.model.response.Incident
import com.example.greensignal.domain.model.response.IncidentAttachment
import com.example.greensignal.domain.model.response.SavedFile
import com.example.greensignal.presentation.event.IncidentEvent
import com.example.greensignal.presentation.event.PetitionEvent
import com.example.greensignal.presentation.state.IncidentState
import com.example.greensignal.presentation.ui.element.AttachmentElement
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

@Composable
fun IncidentScreen(incidentId: String,
                   navController: NavController,
                   state: IncidentState,
                   onEvent: (IncidentEvent) -> Unit) {

    LaunchedEffect(key1 = true) {
        onEvent(IncidentEvent.GetIncident(incidentId))
    }

    if(state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                .verticalScroll(rememberScrollState())
        ) {
            Incident(navController, state, onEvent, incidentId)
        }
    }
}

@Composable
private fun Incident(navController: NavController,
                     state: IncidentState,
                     onEvent: (IncidentEvent) -> Unit,
                     incidentId: String) {

    val showReportDialog =  remember { mutableStateOf(false) }

    if(showReportDialog.value) {
        ReportIncident(show = showReportDialog.value,
            onDismiss = { showReportDialog.value = false },
            onConfirm = { showReportDialog.value = false },
            onEvent = onEvent,
            navController = navController)
    }

    Column {

        HeaderRowBackElement(navController,
                            Screen.IncidentListScreen.route,
                            stringResource(id = R.string.incident_list))

        Column(modifier = Modifier.padding(15.dp)) {

            Text(
                modifier = Modifier,
                fontSize = 20.sp,
                text = state.incident.kind.title,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row {
                Text(
                    text = SimpleDateFormat("dd.MM.yyyy").format(state.incident.createdAt),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.width(30.dp))

                Text(
                    text = state.incident.status.title,
                    color = when (state.incident.status) {
                        IncidentStatus.Draft -> Color.Gray
                        IncidentStatus.Submitted -> Color.Gray
                        IncidentStatus.Attached -> Color.Gray
                        IncidentStatus.Completed -> MaterialTheme.colorScheme.primary
                        IncidentStatus.Closed -> MaterialTheme.colorScheme.error
                        IncidentStatus.Deleted -> MaterialTheme.colorScheme.error
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = state.incident.address,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(15.dp))

            if (state.incident.lat != null && state.incident.lng != null) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            LatLng(
                                state.incident.lat,
                                state.incident.lng
                            ), 15f
                        )
                    },
                ) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                state.incident.lat,
                                state.incident.lng
                            )
                        ),
                        icon = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    )
                }
            } else {
                Text(
                    text = stringResource(id = R.string.coords_are_null),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = stringResource(id = R.string.description),
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = state.incident.description,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(15.dp))

            val columnHeight = state.incident.incidentAttachments.size * 110

            if (state.incident.incidentAttachments.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(columnHeight.dp)
                ) {
                    items(state.incident.incidentAttachments.size) { index ->
                        AttachmentElement(
                            description = state.incident.incidentAttachments[index].description,
                            url = Constants.BASE_URL + "storage/download/" + state.incident.incidentAttachments[index].savedFile.path
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            } else {
                Text(
                    text = "Нет прикреплённых к обращению фотографий",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(15.dp))
            }

            Row {
                Text(
                    text = stringResource(id = R.string.reportedBy),
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = state.incident.reportedBy.rating.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if(state.incident.reportedBy.rating > 6.0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                )
            }

            Text(
                text = state.incident.reportedBy.fio,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.width(15.dp))

            if (state.incident.reportedBy.phone.isNotEmpty())
                Text(
                    text = "+" + formatPhoneNumber(state.incident.reportedBy.phone),
                    style = MaterialTheme.typography.bodyLarge,
                )
            else
                Text(
                    text = "Чтобы просмотреть номер телефона нужно взять нарушение в работу",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )

            Spacer(modifier = Modifier.height(30.dp))

            if (state.incident.inspectorId == null) {
                Button(
                    onClick = {
                        onEvent(IncidentEvent.GetIncidentInWork(incidentId))
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.get_in_work),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        showReportDialog.value = true
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
                    Text(
                        text = stringResource(id = R.string.report_incident),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))
            } else if (state.incident.inspectorId == state.inspectorId) {
                Button(
                    onClick = {
                        navController.navigate(Screen.CreateIncidentReportScreen.withArgs(state.incident.id))
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.create_incident_report),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun ReportIncident(show: Boolean,
                  onDismiss: () -> Unit,
                  onConfirm: () -> Unit,
                  onEvent: (IncidentEvent) -> Unit,
                  navController: NavController) {
    val radioOptions = listOf(ReportType.Unsolvable, ReportType.Unacceptable)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    if (show) {
        Dialog(
            onDismissRequest = {
                onDismiss()
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ElevatedCard(modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .shadow(7.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(Color.White),
                shape = RoundedCornerShape(20.dp),
            ) {
                Box(modifier = Modifier) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.Center)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(id = R.string.close_petition),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )

                            Button(
                                onClick = { onDismiss() },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                            ) {
                                Text("X")
                            }
                        }


                        Spacer(modifier = Modifier.height(20.dp))

                        radioOptions.forEach { status ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (status == selectedOption),
                                        onClick = {
                                            onOptionSelected(status)
                                        }
                                    )
                                    .padding(horizontal = 16.dp)
                            ) {
                                RadioButton(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    selected = (status == selectedOption),
                                    onClick = { onOptionSelected(status) }
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = status.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                onEvent(IncidentEvent.ReportIncident(selectedOption))
                                onConfirm()
                                navController.navigate(Screen.IncidentListScreen.route) {
                                    popUpTo(Screen.IncidentListScreen.route) { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.report_incident),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatPhoneNumber(phoneNumber: String): String {
    val formattedPhoneNumber = PhoneNumberUtils.formatNumber(phoneNumber, "RU")
    return formattedPhoneNumber ?: phoneNumber
}

@Composable
@Preview(name = "Incident",
    showBackground = true,
    heightDp = 1050)
fun IncidentLowPreview() {
    GreenSignalTheme {
        IncidentScreen(
            navController = rememberNavController(),
            state = IncidentState(incident = Incident(address = "Россия, Красноярский край, Красноярский кадастровый район, Кировский район, Апрельская улица, 6Б",
                kind = IncidentKind.Dump, createdAt = Date.from(Instant.now()),
                lat = 52.90, lng = 92.50, description = "Шла Саша по шоссе и сосала сушку",
                incidentAttachments = mutableListOf(IncidentAttachment(
                    description = "Французкие булки",
                    savedFile = SavedFile(path = "")
                )),
                reportedBy = Citizen(
                    fio = "Тестовый тест",
                    phone = "79676049126"
                )
            )),
            onEvent = {},
            incidentId = "123"
        )
    }
}

@Composable
@Preview(name = "Incident",
    showBackground = true,
    heightDp = 1600)
fun IncidentPreview() {
    GreenSignalTheme {
        IncidentScreen(
            navController = rememberNavController(),
            state = IncidentState(incident = Incident(address = "Россия, Красноярский край, Красноярский кадастровый район, Кировский район, Апрельская улица, 6Б",
                kind = IncidentKind.Dump, createdAt = Date.from(Instant.now()),
                lat = 52.90, lng = 92.50, description = "Шла Саша по шоссе и сосала сушку",
                incidentAttachments = mutableListOf(IncidentAttachment(
                    description = "Французкие булки",
                    savedFile = SavedFile(path = "")
                ),
                    IncidentAttachment(
                        description = "Французкие булки",
                        savedFile = SavedFile(path = "")
                    ),
                    IncidentAttachment(
                        description = "Французкие булки",
                        savedFile = SavedFile(path = "")
                    ),
                    IncidentAttachment(
                        description = "Французкие булки",
                        savedFile = SavedFile(path = "")
                    ),
                    IncidentAttachment(
                        description = "Французкие булки",
                        savedFile = SavedFile(path = "")
                    )),
                reportedBy = Citizen(
                    fio = "Тестовый тест",
                    phone = "79676049126",
                    rating = 6.0
                )
            )),
            onEvent = {},
            incidentId = "123"
        )
    }
}

@Composable
@Preview(name = "Incident",
    showBackground = true)
fun IncidentPreviewLoading() {
    GreenSignalTheme {
        IncidentScreen(
            navController = rememberNavController(),
            state = IncidentState(isLoading = true),
            onEvent = {},
            incidentId = "123"
        )
    }
}