package com.example.greensignal.presentation.ui.screen

import android.graphics.Bitmap
import android.graphics.Picture
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.greensignal.R
import com.example.greensignal.presentation.event.UpdateInspectorEvent
import com.example.greensignal.presentation.state.UpdateInspectorState
import com.example.greensignal.presentation.ui.element.DrawingField
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateInspectorScreen(navController: NavController,
                          state: UpdateInspectorState,
                          onEvent: (UpdateInspectorEvent) -> Unit) {
    val picture = remember { Picture() }

    fun createBitmapFromPicture(picture: Picture): Bitmap {
        val bitmap = Bitmap.createBitmap(
            picture.width,
            picture.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawPicture(picture)
        return bitmap
    }

    val galleryLauncherPhoto =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null)
                onEvent(UpdateInspectorEvent.UpdateInspectorPhoto(uri))
        }

    val galleryLauncherCert =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null)
                onEvent(UpdateInspectorEvent.UpdateInspectorCert(uri))
        }

    if(state.isSuccess) {
        navController.navigate(Screen.InspectorProfileScreen.route) {
            popUpTo(Screen.InspectorProfileScreen.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Column {

            HeaderRowBackElement(
                navController,
                Screen.InspectorProfileScreen.route,
                stringResource(id = R.string.my_profile)
            )

            Box(modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
            ) {
                Column {

                    Text(
                        modifier = Modifier.padding(15.dp),
                        color = Color.White,
                        text = stringResource(id = R.string.change_my_profile),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    var refreshKey by remember { mutableStateOf(0) }

                    Row(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(10.dp)) {

                        Box(modifier = Modifier.clickable(onClick = {
                            galleryLauncherPhoto.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )

                        })) {

                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(state.updatePhotoUri)
                                    .crossfade(true)
                                    .build(),
                                loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.padding(20.dp)
                                    )
                                },
                                error = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.padding(20.dp)
                                    )
                                },
                                contentDescription = "photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .size(100.dp)
                            )

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.background, CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                            {
                                Icon(modifier = Modifier
                                    .padding(5.dp),
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit photo",
                                    tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                            Text(
                                text = state.status.value,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.surface,
                                textDecoration = TextDecoration.Underline
                            )

                            OutlinedTextField(
                                value = state.updateFIO,
                                onValueChange = {
                                    onEvent(UpdateInspectorEvent.FioChanged(it))
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    unfocusedBorderColor = Color.White,
                                    focusedBorderColor = Color.White,
                                    cursorColor = Color.White,
                                    unfocusedLabelColor = Color.White,
                                    focusedLabelColor = Color.White),
                                label = { Text(stringResource(id = R.string.user_fio)) },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                                isError = state.updateFIOError != null
                            )
                            state.updateFIOError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Column(
                modifier = Modifier
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = state.updatePhone,
                    onValueChange = {
                        if(it.length < 3) onEvent(UpdateInspectorEvent.PhoneChanged("+7 "))
                        else if (it.length <= 16) onEvent(UpdateInspectorEvent.PhoneChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.phone_number)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.updatePhoneError != null
                )
                state.updatePhoneError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.padding(10.dp))

                OutlinedTextField(
                    value = state.updateCertificateId.take(8),
                    onValueChange = {
                        onEvent(UpdateInspectorEvent.CertificateIdChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.user_cert_id)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.updateCertificateIdError != null
                )
                state.updateCertificateIdError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.padding(10.dp))

                val isOpen = remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = state.updateCertificateDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        label = { Text(stringResource(id = R.string.user_cert_date)) },
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
                            contentDescription = "Calendar"
                        )
                    }
                }

                if (isOpen.value) {
                    val calendar = Calendar.getInstance()
                    calendar.set(
                        LocalDate.now().year,
                        LocalDate.now().month.value,
                        LocalDate.now().dayOfMonth
                    )

                    val datePickerState =
                        rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)

                    DatePickerDialog(
                        onDismissRequest = { },
                        confirmButton = {
                            Button(onClick = {
                                onEvent(
                                    UpdateInspectorEvent.CertificateDateChanged(
                                        LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(datePickerState.selectedDateMillis!!),
                                            ZoneId.systemDefault()
                                        )
                                    )
                                )
                                isOpen.value = false
                            }) {
                                Text(stringResource(id = R.string.apply))
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
                                    text = stringResource(id = R.string.select_date_cert),
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

                Spacer(modifier = Modifier.padding(10.dp))

                OutlinedTextField(
                    value = state.updateSchoolId.take(12),
                    onValueChange = {
                        onEvent(UpdateInspectorEvent.SchoolIdChanged(it))
                    },
                    label = { Text(stringResource(id = R.string.user_school_id)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.updateSchoolIdError != null
                )
                state.updateSchoolIdError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.padding(10.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(id = R.string.documents),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .horizontalScroll(rememberScrollState())) {

                    Box(modifier = Modifier.clickable(onClick = {
                        galleryLauncherCert.launch(
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    })) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(state.updateCertificateUri)
                                .crossfade(true)
                                .build(),
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(20.dp)
                                )
                            },
                            error = {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(20.dp)
                                )
                            },
                            contentDescription = "photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .background(Color.White)
                                .size(125.dp)
                        )

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.background, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                        {
                            Icon(modifier = Modifier
                                .padding(5.dp),
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit photo",
                                tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(10.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.padding(15.dp))

                DrawingField(picture = picture,
                    isSaveVisible = true,
                    { onEvent(UpdateInspectorEvent.UpdateInspectorSignature(createBitmapFromPicture(picture))) }
                )

                Spacer(modifier = Modifier.padding(15.dp))

                Button(
                    onClick = {
                        onEvent(UpdateInspectorEvent.UpdateInspector)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                ) {
                    if (state.isLoading)
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(2.dp)
                                .align(Alignment.CenterVertically),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    else Text(
                        text = stringResource(id = R.string.save),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Spacer(modifier = Modifier.padding(15.dp))
            }
        }
    }
}

@Composable
@Preview(showBackground = true, heightDp = 1500)
fun UpdateInspectorPreview()
{
    GreenSignalTheme {
        UpdateInspectorScreen(navController = rememberNavController(),
            state = UpdateInspectorState(updateFIO = "Шибанова Валентина Сергеевна"),
            onEvent = {})
    }
}