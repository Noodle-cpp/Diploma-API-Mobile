package com.example.greensignal.presentation.ui.element

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.greensignal.R
import com.example.greensignal.domain.model.interfaces.IIncidentReportAttachment
import com.example.greensignal.presentation.ui.dialog.IncidentReportAttachmentDialog
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
inline fun <reified T: IIncidentReportAttachment> AddedIncidentReportAttachmentElement(description: String,
                                                                                       url: Uri,
                                                                                       date: String,
                                                                                       index: Int,
                                                                                       files: MutableList<T>,
                                                                                       crossinline onDeleteAction: () -> Unit
) {
    val showDialog =  remember { mutableStateOf(false) }

    if(showDialog.value)
        IncidentReportAttachmentDialog(setShowDialog = {
            showDialog.value = it
        }, files, index)

    Column {
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = url,
                contentDescription = description,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
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

                Spacer(modifier = Modifier.height(15.dp))

                Row {
                    Text(
                        text = stringResource(id = R.string.change),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.clickable(onClick =
                        {
                            showDialog.value = true
                        })
                    )

                    Spacer(modifier = Modifier.width(30.dp))

                    Text(
                        text = stringResource(id = R.string.delete),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.clickable(onClick =
                        {
                            files.removeAt(index)
                            onDeleteAction()
                        })
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Photo Element",
    showBackground = true)
@Composable
fun AddedIncidentReportAttachmentPreview() {
    GreenSignalTheme {
        AddedIncidentReportAttachmentElement(
            "съешь же ещё этих мягких французских булок, да выпей чаю. съешь же ещё этих мягких французских булок, да выпей чаю",
            Uri.EMPTY,
            "2024-03-24T15:18:01.001Z",
            0,
            mutableListOf(),
            onDeleteAction = {}
        )
    }
}