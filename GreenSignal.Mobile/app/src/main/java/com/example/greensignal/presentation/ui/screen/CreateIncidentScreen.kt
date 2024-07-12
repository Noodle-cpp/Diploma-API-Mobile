package com.example.greensignal.presentation.ui.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.annotation.RequiresPermission
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.presentation.event.CreateIncidentEvent
import com.example.greensignal.presentation.state.CreateIncidentState
import com.example.greensignal.presentation.ui.dialog.PermissionDialog
import com.example.greensignal.presentation.ui.element.AddPhotoElement
import com.example.greensignal.presentation.ui.element.AddedAttachmentElement
import com.example.greensignal.presentation.ui.element.IncidentKindElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun CreateIncidentScreen(
    navController: NavController,
    state: CreateIncidentState,
    onEvent: (CreateIncidentEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        } else {

            when (state.step) {
                1 -> {
                    CreateIncidentStepOne(navController, state, onEvent)
                }

                2 -> {
                    CreateIncidentStepTwo(navController, state, onEvent)
                }

                3 -> {
                    CurrentLocationScreen(navController, state, onEvent)
                }

                4 -> {
                    CreateIncidentStepFour(navController, state, onEvent)
                }

                5 -> {
                    CreateIncidentStepFive(navController, state, onEvent)
                }

                6 -> {
                    CreateIncidentStepSix(navController)
                }
            }
        }
    }
}

@Composable
fun CreateIncidentStepOne(navController: NavController, state: CreateIncidentState, onEvent: (CreateIncidentEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        HeaderRow(
            navController = navController,
            text = stringResource(id = R.string.home_screen),
            onEvent,
            state
        )

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
                .align(Alignment.CenterHorizontally)
        ) {

            Text(
                text = stringResource(id = R.string.create_incident),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(15.dp))

            CreateIncidentBody("1", stringResource(id = R.string.choose_incident_kind))


            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                IncidentKindElement(
                    stringResource(id = R.string.dump),
                    "Dump",
                    ImageBitmap.imageResource(R.drawable.junk),
                    onEvent,
                    IncidentKind.Dump,
                    state.kind == IncidentKind.Dump
                )

                Spacer(modifier = Modifier.width(30.dp))

                IncidentKindElement(
                    stringResource(id = R.string.pollution),
                    "Pollution",
                    ImageBitmap.imageResource(R.drawable.jerrycan),
                    onEvent,
                    IncidentKind.SoilPollution,
                    state.kind == IncidentKind.SoilPollution
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                IncidentKindElement(
                    stringResource(id = R.string.excavation),
                    "Excavation",
                    ImageBitmap.imageResource(R.drawable.excav),
                    onEvent,
                    IncidentKind.Excavation,
                    state.kind == IncidentKind.Excavation
                )

                Spacer(modifier = Modifier.width(30.dp))

                IncidentKindElement(
                    stringResource(id = R.string.treeCutting),
                    "TreeCutting",
                    ImageBitmap.imageResource(R.drawable.tree),
                    onEvent,
                    IncidentKind.TreeCutting,
                    state.kind == IncidentKind.TreeCutting
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                IncidentKindElement(
                    stringResource(id = R.string.airPollution),
                    "AirPollution",
                    ImageBitmap.imageResource(R.drawable.pollution),
                    onEvent,
                    IncidentKind.AirPollution,
                    state.kind == IncidentKind.AirPollution
                )

                Spacer(modifier = Modifier.width(30.dp))

                IncidentKindElement(
                    stringResource(id = R.string.radiation),
                    "Radiation",
                    ImageBitmap.imageResource(R.drawable.radiation),
                    onEvent,
                    IncidentKind.Radiation,
                    state.kind == IncidentKind.Radiation
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CreateIncidentStepTwo(navController: NavController,
                          state: CreateIncidentState,
                          onEvent: (CreateIncidentEvent) -> Unit) {
    Column (modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        HeaderRow(navController = navController, text = stringResource(id = R.string.prev_step), onEvent, state)

        Spacer(modifier = Modifier.height(15.dp))

        Column(modifier = Modifier.padding(15.dp)) {
            Text(
                text = stringResource(id = R.string.create_incident),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(15.dp))

            CreateIncidentBody("2", stringResource(id = R.string.description_incident))

            OutlinedTextField(
                value = state.description,
                onValueChange = {
                    onEvent(CreateIncidentEvent.DescriprionChanged(it))
                },
                label = { Text(stringResource(id = R.string.description)) },
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onEvent(CreateIncidentEvent.NextStep) }
                ),
                isError = state.descriptionError != null
            )
            state.descriptionError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onEvent(CreateIncidentEvent.NextStep)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = stringResource(id = R.string.next_step),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun CurrentLocationScreen(navController: NavController,
                          state: CreateIncidentState,
                          onEvent: (CreateIncidentEvent) -> Unit) {
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
            CreateIncidentStepThree(
                navController, state, onEvent,
                usePreciseLocation = it.contains(android.Manifest.permission.ACCESS_FINE_LOCATION),
            )
        },
    )
}

@OptIn(MapsComposeExperimentalApi::class)
@RequiresPermission(
    anyOf = [android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun CreateIncidentStepThree(navController: NavController,
                            state: CreateIncidentState,
                            onEvent: (CreateIncidentEvent) -> Unit,
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
            val result = locationClient.getCurrentLocation(
                priority,
                CancellationTokenSource().token,
            ).await()
            result?.let { fetchedLocation ->
                onEvent(CreateIncidentEvent.GetLocation(fetchedLocation.latitude, fetchedLocation.longitude, context))
            }
        }
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        HeaderRow(
            navController = navController,
            text = stringResource(id = R.string.prev_step),
            onEvent,
            state
        )

        Spacer(modifier = Modifier.height(15.dp))

        Column(modifier = Modifier.padding(15.dp)) {

            Text(
                text = stringResource(id = R.string.create_incident),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(15.dp))

            CreateIncidentBody("3", stringResource(id = R.string.select_place))

            if(state.lat != null && state.lng != null) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(LatLng(state.lat, state.lng), 15f)
                    },
                    onMapClick = {
                        onEvent(CreateIncidentEvent.OnMapClick(it, context))
                    }

                ) {
                    Marker(
                        state = MarkerState(position = LatLng(state.lat, state.lng)),
                        icon = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    )
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

            OutlinedTextField(
                value = state.address,
                onValueChange = {
                    onEvent(CreateIncidentEvent.AddressChanged(it))
                },
                label = { Text(stringResource(id = R.string.address)) },
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onEvent(CreateIncidentEvent.NextStep) }
                ),
                isError = state.addressError != null
            )

            state.addressError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onEvent(CreateIncidentEvent.NextStep) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = stringResource(id = R.string.next_step),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
fun CreateIncidentStepFour(navController: NavController,
                          state: CreateIncidentState,
                          onEvent: (CreateIncidentEvent) -> Unit) {
    Column (modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        HeaderRow(navController = navController, text = stringResource(id = R.string.prev_step), onEvent, state)

        Spacer(modifier = Modifier.height(15.dp))

        Column(modifier = Modifier.padding(15.dp)) {

            Text(
                text = stringResource(id = R.string.create_incident),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(15.dp))

            CreateIncidentBody("4", stringResource(id = R.string.select_photo))

            val columnHeight = state.files.size * 110

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(columnHeight.dp),
            ) {
                itemsIndexed(state.files) { index, file ->
                    AddedAttachmentElement(
                        description = file.description.value,
                        url = file.file.value,
                        index,
                        state.files
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            AddPhotoElement(state.files)

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { onEvent(CreateIncidentEvent.NextStep) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = stringResource(id = R.string.next_step),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun CreateIncidentStepFive(navController: NavController,
                          state: CreateIncidentState,
                          onEvent: (CreateIncidentEvent) -> Unit) {
    val focusManager = LocalFocusManager.current

    Column (modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        HeaderRow(
            navController = navController,
            text = stringResource(id = R.string.prev_step),
            onEvent,
            state
        )

        Spacer(modifier = Modifier.height(15.dp))

        Column(modifier = Modifier.padding(15.dp)) {

            Text(
                text = stringResource(id = R.string.create_incident),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(15.dp))

            CreateIncidentBody("5", stringResource(id = R.string.set_user_data))

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = state.citizenFIO,
                onValueChange = {
                    onEvent(CreateIncidentEvent.CitizenFIOChanged(it))
                },
                label = { Text(stringResource(id = R.string.user_fio)) },
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onEvent(CreateIncidentEvent.NextStep) }
                ),
                isError = state.citizenFIOError != null
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = state.citizenPhone,
                onValueChange = {
                    if (it.length < 3) onEvent(CreateIncidentEvent.CitizenPhoneChanged("+7 "))
                    else if (it.length <= 16) onEvent(CreateIncidentEvent.CitizenPhoneChanged(it))
                },
                label = { Text(stringResource(id = R.string.phone_number)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                isError = state.citizenPhoneError != null
            )
            state.citizenPhoneError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Row {
                OutlinedTextField(
                    value = state.code,
                    onValueChange = {
                        if (it.length <= 4) onEvent(CreateIncidentEvent.CodeChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.sms_code)) },
                    singleLine = true,
                    modifier = Modifier.width(150.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    shape = RoundedCornerShape(
                        topStart = 5.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 5.dp,
                    ),
                    isError = state.codeError != null
                )

                Button(
                    onClick = {
                        onEvent(CreateIncidentEvent.GetCode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp, 0.dp, 0.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 5.dp,
                        bottomEnd = 5.dp,
                        bottomStart = 0.dp,
                    ),
                ) {
                    if (state.isCodeLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(2.dp)
                                .align(Alignment.CenterVertically),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.get_code),
                            fontSize = 15.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onEvent(CreateIncidentEvent.CreateIncident)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = stringResource(id = R.string.create_incident),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
fun CreateIncidentStepSix(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()) {

        Text(text = stringResource(id = R.string.create_incident_congrats),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                navController.navigate(Screen.HomeScreen.route) {
                    popUpTo(Screen.HomeScreen.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(horizontal = 100.dp)
        ) {
            Text(
                text = stringResource(id = R.string.ok_text),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}


@Composable
fun HeaderRow(navController: NavController,
              text: String,
              onEvent: (CreateIncidentEvent) -> Unit,
              state: CreateIncidentState) {
    Box(modifier = Modifier
        .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart)
    {
        Column(modifier = Modifier) {
            Spacer(modifier = Modifier.height(15.dp))

            Row(modifier = Modifier
                .clickable {
                    if(state.step > 1) onEvent(CreateIncidentEvent.PrevStep)
                    else  navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { inclusive = true }
                    }
                }) {

                Row {
                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        modifier = Modifier,
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

    }
}

@Composable
fun CreateIncidentBody(step: String, action: String) {
    Column {
            Text(text = (stringResource(id = R.string.step) + " " + step + "/5:"),
                fontSize = 20.sp)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = action,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(15.dp))
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Create Incident Step 1",
    showBackground = true,
    heightDp = 1000)
@Composable
fun CreateIncidentStepOnePreview() {
    GreenSignalTheme {
        CreateIncidentScreen(
            navController = rememberNavController(),
            state = CreateIncidentState(step = 1),
            onEvent = {}
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Create Incident Loading",
    showBackground = true)
@Composable
fun CreateIncidentLoadingPreview() {
    GreenSignalTheme {
        CreateIncidentScreen(
            navController = rememberNavController(),
            state = CreateIncidentState(isLoading = true),
            onEvent = {}
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Create Incident Step 2",
    showBackground = true)
@Composable
fun CreateIncidentStepTwoPreview() {
    GreenSignalTheme {
        CreateIncidentScreen(
            navController = rememberNavController(),
            state = CreateIncidentState(step = 2),
            onEvent = {}
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Create Incident Step 3",
    showBackground = true)
@Composable
fun CreateIncidentStepThreePreview() {
    GreenSignalTheme {
        CreateIncidentScreen(
            navController = rememberNavController(),
            state = CreateIncidentState(step = 3),
            onEvent = {}
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Create Incident Step 3",
    showBackground = true)
@Composable
fun CreateIncidentStepThreePermissionPreview() {
    GreenSignalTheme {
        CreateIncidentScreen(
            navController = rememberNavController(),
            state = CreateIncidentState(step = 3 ),
            onEvent = {}
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Create Incident Step 4",
    showBackground = true)
@Composable
fun CreateIncidentStepFourPreview() {
    GreenSignalTheme {
        CreateIncidentScreen(
            navController = rememberNavController(),
            state = CreateIncidentState(step = 4),
            onEvent = {}
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Create Incident Step 5",
    showBackground = true)
@Composable
fun CreateIncidentStepFivePreview() {
    GreenSignalTheme {
        CreateIncidentScreen(
            navController = rememberNavController(),
            state = CreateIncidentState(step = 5),
            onEvent = {},
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Create Incident Step 6",
    showBackground = true)
@Composable
fun CreateIncidentStepSixPreview() {
    GreenSignalTheme {
        CreateIncidentScreen(
            navController = rememberNavController(),
            state = CreateIncidentState(step = 6),
            onEvent = {},
        )
    }
}

