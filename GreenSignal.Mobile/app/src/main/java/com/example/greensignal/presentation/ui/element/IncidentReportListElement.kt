package com.example.greensignal.presentation.ui.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
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
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus
import com.example.greensignal.domain.model.response.IncidentReport
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Composable
fun IncidentReportListElement(incidentReport: IncidentReport, navController: NavController) {
    Column(modifier = Modifier.clickable(onClick = {
        navController.navigate(Screen.IncidentReportScreen.withArgs(incidentReport.id))
    })) {

        Spacer(modifier = Modifier.height(15.dp))

        Row(modifier = Modifier.fillMaxWidth()) {

            Image(
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.CenterVertically),
                bitmap = when (incidentReport.kind) {
                    IncidentReportKind.AirPollution -> ImageBitmap.imageResource(R.drawable.pollution)
                    IncidentReportKind.SoilPollution -> ImageBitmap.imageResource(R.drawable.jerrycan)
                    IncidentReportKind.Excavation -> ImageBitmap.imageResource(R.drawable.excav)
                    IncidentReportKind.TreeCutting -> ImageBitmap.imageResource(R.drawable.tree)
                    IncidentReportKind.Radiation -> ImageBitmap.imageResource(R.drawable.radiation)
                    IncidentReportKind.Dump -> ImageBitmap.imageResource(R.drawable.junk)
                },
                contentDescription = "incident_kind_icon",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.width(10.dp))


            Column {

                Text(
                    text = "Акт №${incidentReport.serialNumber} - ${incidentReport.kind.title}",
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = incidentReport.address,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row {
                    Text(
                        text = SimpleDateFormat("dd.MM.yyyy").format(incidentReport.createdAt),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Spacer(modifier = Modifier.width(30.dp))

                    Text(
                        text = incidentReport.status.title,
                        color = when(incidentReport.status) {
                            IncidentReportStatus.Draft -> Color.Gray
                            IncidentReportStatus.Sent -> Color.Gray
                            IncidentReportStatus.Completed_successfuly -> MaterialTheme.colorScheme.primary
                            IncidentReportStatus.Completed_unsucessful -> MaterialTheme.colorScheme.error
                            IncidentReportStatus.Archived -> MaterialTheme.colorScheme.error
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

@Composable
@Preview(showBackground = true)
fun IncidentReportListElementPreview() {
    GreenSignalTheme {
        IncidentReportListElement(navController = rememberNavController(),
            incidentReport = IncidentReport(
                serialNumber = "1-1",
                address = "Улица Пушкина дом Калатушкина",
                manualDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
                startOfInspection = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
                endOfInspection = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
                status = IncidentReportStatus.Draft,
                kind = IncidentReportKind.Dump,
                lat = 92.56,
                lng = 56.92,
            )
        )
    }
}