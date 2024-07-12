package com.example.greensignal.presentation.ui.dialog

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.greensignal.R
import com.example.greensignal.domain.model.request.CreateIncidentAttachment
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme

@Composable
fun ImageDialog(
    setShowDialog: (Boolean) -> Unit,
    files: MutableList<CreateIncidentAttachment>,
    index: Int? = null
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }

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
        }
    }

    Dialog(onDismissRequest = {
        setShowDialog(false)
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
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
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    )
                )
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
                                }
                                else {
                                    files.add(CreateIncidentAttachment(mutableStateOf(imageUri!!), mutableStateOf(description)))
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
fun ImageDialogPreview() {
    GreenSignalTheme {
        ImageDialog(
            setShowDialog = {

            },
            mutableListOf<CreateIncidentAttachment>(),
        )
    }
}