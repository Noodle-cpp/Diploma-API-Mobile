package com.example.greensignal.presentation.ui.element

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ArgumentFieldElement(title: String, argument: String) {
    Column {
        Spacer(modifier = Modifier.height(10.dp))

        if(title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(5.dp))
        }

        Text(
            text = argument,
            style = MaterialTheme.typography.titleSmall
        )
    }
}