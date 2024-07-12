package com.example.greensignal.presentation.ui.element

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greensignal.R
import com.example.greensignal.domain.model.interfaces.IIncidentReportAttachment
import com.example.greensignal.domain.model.request.CreateIncidentAttachment
import com.example.greensignal.presentation.ui.dialog.ImageDialog
import com.example.greensignal.presentation.ui.dialog.IncidentReportAttachmentDialog
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme

@Composable
fun AddPhotoElement(files: MutableList<CreateIncidentAttachment>) {
    val showDialog =  remember { mutableStateOf(false) }

    if(showDialog.value)
        ImageDialog(setShowDialog = {
            showDialog.value = it
        }, files)

    Row(modifier = Modifier.clickable(onClick = {
        showDialog.value = true
    })) {
        Icon(modifier = Modifier
            .size(30.dp)
            .align(Alignment.CenterVertically),
            imageVector = Icons.Default.Add,
            contentDescription = "AddPhoto",
            tint = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.width(10.dp))

        Text(text = stringResource(id = R.string.add_photo),
            modifier = Modifier.align(Alignment.CenterVertically),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
inline fun <reified T: IIncidentReportAttachment> AddPhotoIncidentReportElement(files: MutableList<T>) {
    val showDialog =  remember { mutableStateOf(false) }

    if(showDialog.value)
        IncidentReportAttachmentDialog(setShowDialog = {
            showDialog.value = it
        }, files)

    Row(modifier = Modifier.clickable(onClick = {
        showDialog.value = true
    })) {
        Icon(modifier = Modifier
            .size(30.dp)
            .align(Alignment.CenterVertically),
            imageVector = Icons.Default.Add,
            contentDescription = "AddPhoto",
            tint = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.width(10.dp))

        Text(text = stringResource(id = R.string.add_photo),
            modifier = Modifier.align(Alignment.CenterVertically),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun AddPhotoElement(file: MutableState<Uri>) {
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null)
                file.value = uri
        }

    Row(modifier = Modifier.clickable(onClick = {
        galleryLauncher.launch(
            PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    })) {
        Icon(modifier = Modifier
            .size(30.dp)
            .align(Alignment.CenterVertically),
            imageVector = Icons.Default.Add,
            contentDescription = "AddPhoto",
            tint = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.width(10.dp))

        Text(text = stringResource(id = R.string.add_photo),
            modifier = Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary)
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Add Photo Element",
    showBackground = true)
@Composable
fun AddPhotoElementPreview() {
    GreenSignalTheme {
        AddPhotoElement(mutableListOf<CreateIncidentAttachment>())
    }
}