package com.example.greensignal.presentation.ui.element

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.greensignal.presentation.ui.dialog.FullScreenPhotoDialog
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun IncidentReportAttachmentElement(description: String, url: String, date: String) {
    var isError by remember { mutableStateOf(false) }
    val showPhotoDialog = remember { mutableStateOf(false) }
    var photoUrl by remember { mutableStateOf(url) }

    if(showPhotoDialog.value)
        FullScreenPhotoDialog(setShowDialog = {
            showPhotoDialog.value = it
        }, url)

    Column(modifier = Modifier.clickable(onClick = {
        if (!isError)
            showPhotoDialog.value = true
    })) {
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                loading = {
                    CircularProgressIndicator()
                    isError = false
                },
                error = {
                    Icon(
                        modifier = Modifier.fillMaxSize().clickable(onClick = {
                            photoUrl += "?"
                        }),
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Photo exception",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    isError = true
                },
                success = {
                    SubcomposeAsyncImageContent()
                    isError = false
                },
                contentDescription = "attachment",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(5.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                val formatPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                val formatter = DateTimeFormatter.ofPattern(formatPattern)
                val localDateTime = LocalDateTime.parse(date, formatter)

                Text(text = localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.End))

                Text(text = description,
                    style = MaterialTheme.typography.bodyMedium)

            }
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Photo Element",
    showBackground = true)
@Composable
fun IncidentReportAttachmentElementAttachmentPreview() {
    GreenSignalTheme {
        IncidentReportAttachmentElement(
            "съешь же ещё этих мягких французских булок, да выпей чаю. съешь же ещё этих мягких французских булок, да выпей чаю",
            "",
            "2024-03-24T15:18:01.001Z",
        )
    }
}