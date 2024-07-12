package com.example.greensignal.presentation.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme

@Composable
fun FullScreenPhotoDialog(setShowDialog: (Boolean) -> Unit, url: String) {
    Dialog(
        onDismissRequest = {
            setShowDialog(false)
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.5f),
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { setShowDialog(false) },
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text("X")
                }

                Spacer(modifier = Modifier.height(10.dp))

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(70.dp)
                        )
                    },
                    error = {
                        Icon(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Photo exception",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    success = {
                        SubcomposeAsyncImageContent()
                    },
                    contentDescription = "photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FullScreenPhotoDialogPreview() {
    GreenSignalTheme {
        FullScreenPhotoDialog(setShowDialog = {

        }, "")
    }
}