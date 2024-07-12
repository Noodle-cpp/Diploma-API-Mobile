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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.PetitionKind
import com.example.greensignal.data.remote.dto.response.PetitionStatus
import com.example.greensignal.domain.model.response.IncidentReport
import com.example.greensignal.domain.model.response.Petition
import com.example.greensignal.domain.model.response.Incident
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun PetitionListElement(petition: Petition, navController: NavController) {
    Column(modifier = Modifier.clickable(onClick = {
        navController.navigate(Screen.PetitionScreen.withArgs(petition.id))
    })) {

        Spacer(modifier = Modifier.height(15.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.CenterVertically),
                bitmap = when (petition.kind) {
                    PetitionKind.AirPollution -> ImageBitmap.imageResource(R.drawable.pollution)
                    PetitionKind.SoilPollution -> ImageBitmap.imageResource(R.drawable.jerrycan)
                    PetitionKind.Excavation -> ImageBitmap.imageResource(R.drawable.excav)
                    PetitionKind.TreeCutting -> ImageBitmap.imageResource(R.drawable.tree)
                    PetitionKind.Radiation -> ImageBitmap.imageResource(R.drawable.radiation)
                    PetitionKind.Dump -> ImageBitmap.imageResource(R.drawable.junk)
                },
                contentDescription = "incident_kind_icon",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {

                Text(
                    text = "Обращение №${petition.serialNumber}, ${if(petition.incidentReport != null) 
                                                                            petition.incidentReport.address
                                                                            else petition.parentPetition!!.incidentReport!!.address}",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = petition.kind.title,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row {

                    Text(
                        text = SimpleDateFormat("dd.MM.yyyy").format(petition.date),
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Spacer(modifier = Modifier.width(30.dp))

                    if(petition.status == PetitionStatus.Sent) {
                        Text(
                            text = "ожид. ответа ${Date().day.minus(petition.createdAt.day)} дней",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    else {
                        Text(
                            text = petition.status.title,
                            color = when (petition.status) {
                                PetitionStatus.Draft -> Color.Gray
                                PetitionStatus.Sent -> Color.Gray
                                PetitionStatus.Replied -> Color.Blue
                                PetitionStatus.Success -> MaterialTheme.colorScheme.primary
                                PetitionStatus.Failed -> MaterialTheme.colorScheme.error
                                PetitionStatus.Archived -> MaterialTheme.colorScheme.error
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
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
fun PetitionListElementPreview() {
    GreenSignalTheme {
        PetitionListElement(petition = Petition(
        serialNumber = "11-12", status = PetitionStatus.Sent, kind = PetitionKind.Dump,
            incidentReport = IncidentReport(
                incident = Incident(
                    address = "Улица Пушкина дом Калатушкина номер 1"
                )
            ),
            date = Date()
        ), navController = rememberNavController())
    }
}