package com.example.greensignal.presentation.ui.dialog

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme


@Composable
fun NetworkErrorDialog(
    retryAction: () -> Unit,
    desc: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .offset(y = -50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "no internet connection",
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.CenterHorizontally),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(id = R.string.no_internet_connection),//),R.string.NoInternetConnection),
                 style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = desc,//stringResource(id = R.string.no_internet_connection_sub),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    retryAction()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.try_again)
                )
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("",
    showBackground = true)
@Composable
fun NetworkErrorDialogPreview() {
    GreenSignalTheme {
        NetworkErrorDialog({  }, "Проверьте ваше интернет подключение")
    }
}