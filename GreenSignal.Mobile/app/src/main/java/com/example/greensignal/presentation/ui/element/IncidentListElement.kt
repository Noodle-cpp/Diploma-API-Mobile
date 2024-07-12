package com.example.greensignal.presentation.ui.element

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import com.example.greensignal.domain.model.response.Incident
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@Composable
fun IncidentListElement(incident: Incident, navController: NavController, isInspectorView: Boolean) {
    Column(modifier = Modifier.clickable(onClick = {
        if(isInspectorView) navController.navigate(Screen.IncidentScreen.withArgs(incident.id))
    })) {

        Spacer(modifier = Modifier.height(15.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            if(isInspectorView) {
                Image(
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.CenterVertically),
                    bitmap = when (incident.kind) {
                        IncidentKind.AirPollution -> ImageBitmap.imageResource(R.drawable.pollution)
                        IncidentKind.SoilPollution -> ImageBitmap.imageResource(R.drawable.jerrycan)
                        IncidentKind.Excavation -> ImageBitmap.imageResource(R.drawable.excav)
                        IncidentKind.TreeCutting -> ImageBitmap.imageResource(R.drawable.tree)
                        IncidentKind.Radiation -> ImageBitmap.imageResource(R.drawable.radiation)
                        IncidentKind.Dump -> ImageBitmap.imageResource(R.drawable.junk)
                    },
                    contentDescription = "incident_kind_icon",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )

                Spacer(modifier = Modifier.width(10.dp))
            }

            Column {

                Text(
                    text = incident.kind.title,
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = incident.address,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row {
                    Text(
                        text = SimpleDateFormat("dd.MM.yyyy").format(incident.createdAt),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Spacer(modifier = Modifier.width(30.dp))

                    Text(
                        text = incident.status.title,
                        color = when(incident.status) {
                            IncidentStatus.Draft -> Color.Gray
                            IncidentStatus.Submitted -> Color.Gray
                            IncidentStatus.Attached -> Color.Gray
                            IncidentStatus.Completed -> MaterialTheme.colorScheme.primary
                            IncidentStatus.Closed -> MaterialTheme.colorScheme.error
                            IncidentStatus.Deleted -> MaterialTheme.colorScheme.error
                        },
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = Color.LightGray
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Photo Element",
    showBackground = true)
@Composable
fun IncidentListElementPreview() {
    GreenSignalTheme {
        IncidentListElement(Incident(address = "Россия, Красноярский край, Красноярский кадастровый район, Кировский район, Апрельская улица, 6Б",
                                        kind = IncidentKind.Dump, createdAt = Date.from(Instant.now()), status = IncidentStatus.Submitted
        ),
            navController = rememberNavController(),
            true
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Photo Element Citizen",
    showBackground = true)
@Composable
fun IncidentListCitizenElementPreview() {
    GreenSignalTheme {
        IncidentListElement(Incident(address = "Россия, Красноярский край, Красноярский кадастровый район, Кировский район, Апрельская улица, 6Б",
                                        kind = IncidentKind.Dump, createdAt = Date.from(Instant.now()), status = IncidentStatus.Submitted
        ),
            navController = rememberNavController(),
            false
        )
    }
}