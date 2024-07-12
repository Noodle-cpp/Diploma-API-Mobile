package com.example.greensignal.presentation.ui.dialog

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.greensignal.R
import com.example.greensignal.domain.model.request.CreateIncidentReportAttachment
import com.example.greensignal.domain.model.interfaces.IIncidentReportAttachment
import com.example.greensignal.domain.model.request.UpdateIncidentReportAttachment
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.lang.reflect.ParameterizedType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <reified T: IIncidentReportAttachment> IncidentReportAttachmentDialog(
    crossinline setShowDialog: (Boolean) -> Unit,
    files: MutableList<T>,
    index: Int? = null,
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }
    var manualDate by remember { mutableStateOf(if(index == null) LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")) else files[index].manualDate.value) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null)
                imageUri = uri
        }

    LaunchedEffect(true) {
        if(index == null) {
            galleryLauncher.launch(
                PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        } else {
            description = files[index].description.value
            imageUri = files[index].file.value
            manualDate = files[index].manualDate.value
        }
    }

    val isOpen = remember { mutableStateOf(false)}
    val date: MutableState<LocalDateTime> = remember { mutableStateOf ( LocalDateTime.parse(manualDate, formatter)) }

    Dialog(onDismissRequest = {
        setShowDialog(false)
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(425.dp)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                AsyncImage(model = imageUri,
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(160.dp)
                        .padding(10.dp)
                        .clickable(onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = { Text(stringResource(id = R.string.description)) },
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    )
                )

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    OutlinedTextField(
                        readOnly = true,
                        value = date.value.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        label = { Text(stringResource(id = R.string.date)) },
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

                if (isOpen.value) {
                    val calendar = Calendar.getInstance()
                    calendar.set(LocalDate.now().year, LocalDate.now().month.value, LocalDate.now().dayOfMonth)

                    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)

                    DatePickerDialog(
                        onDismissRequest = { },
                        confirmButton = {
                            Button(onClick = {
                                date.value = LocalDateTime.ofInstant(Instant.ofEpochMilli(datePickerState.selectedDateMillis!!), ZoneId.systemDefault())
                                manualDate = date.value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
                                isOpen.value = false
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { setShowDialog(false) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Отмена")
                    }
                    TextButton(
                        onClick = {
                            if (imageUri != null) {
                                if(index != null){
                                    files[index].description.value = description
                                    files[index].file.value = imageUri!!
                                    files[index].manualDate.value = manualDate
                                }
                                else {
                                    val newAttachment = when (T::class) {
                                        CreateIncidentReportAttachment::class -> CreateIncidentReportAttachment(
                                            mutableStateOf(imageUri!!),
                                            mutableStateOf(description),
                                            mutableStateOf(manualDate)
                                        )
                                        UpdateIncidentReportAttachment::class -> UpdateIncidentReportAttachment(
                                            null,
                                            mutableStateOf(imageUri!!),
                                            mutableStateOf(description),
                                            mutableStateOf(manualDate)
                                        )
                                        else -> throw IllegalArgumentException("Unknown type for T")
                                    }

                                    files.add(newAttachment as T)
                                }
                                setShowDialog(false)
                            }
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = imageUri != null && !description.isNullOrBlank()
                    ) {
                        Text("Подтвердить")
                    }
                }
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Image Dialog",
    showBackground = true)
@Composable
fun IncidentReportAttachmentDialogPreview() {
    GreenSignalTheme {
        IncidentReportAttachmentDialog(
            setShowDialog = {

            },
            mutableListOf(),
        )
    }
}