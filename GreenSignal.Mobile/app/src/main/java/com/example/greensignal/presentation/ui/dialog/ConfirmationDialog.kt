package com.example.greensignal.presentation.ui.dialog

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme

@Composable
fun ConfirmationDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            title = {
                Text(
                    text = "Подтверждение",
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            text = {
                Text(
                    text = "Вы уверены?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                })
                {
                    Text(
                        text = "Да",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismiss()
                })
                {
                    Text(
                        text = "Нет",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Confirmation Dialog",
    showBackground = true)
@Composable
fun ConfirmationDialogPreview() {
    GreenSignalTheme {
        ConfirmationDialog(
            true,
            onConfirm = {},
            onDismiss = {}
        )
    }
}