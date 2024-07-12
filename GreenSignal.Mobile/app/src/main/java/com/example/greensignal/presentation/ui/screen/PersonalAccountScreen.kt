package com.example.greensignal.presentation.ui.screen

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.presentation.event.InspectorAccountEvent
import com.example.greensignal.presentation.event.PersonalAccountEvent
import com.example.greensignal.presentation.state.InspectorProfileState
import com.example.greensignal.presentation.state.PersonalAccountState
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.element.PersonalAccountMenuElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme

@Composable
fun PersonalAccountScreen(navController: NavController,
                          state: PersonalAccountState,
                          onEvent: (PersonalAccountEvent) -> Unit) {
    LaunchedEffect(key1 = !state.isAuthorized) {
        if(!state.isAuthorized) {
            navController.navigate(Screen.AuthenticationScreen.route) {
                popUpTo(Screen.AuthenticationScreen.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            HeaderRowBackElement(navController = navController, route = Screen.HomeScreen.route, title = stringResource(id = R.string.home_screen))

            Column(modifier = Modifier.padding(15.dp)) {

                Text(
                    text = stringResource(id = R.string.my_inspector_profile_title),
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(15.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        PersonalAccountMenuElement(
                            stringResource(id = R.string.incident_list),
                            "Incident list",
                            ImageBitmap.imageResource(R.drawable.megaphone),
                            Screen.IncidentListScreen.route,
                            navController
                        )

                        Spacer(modifier = Modifier.width(30.dp))

                        PersonalAccountMenuElement(
                            stringResource(id = R.string.my_acts),
                            "My acts",
                            ImageBitmap.imageResource(R.drawable.exam),
                            Screen.IncidentReportListScreen.route,
                            navController
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        PersonalAccountMenuElement(
                            stringResource(id = R.string.my_petitions),
                            "Incident reports",
                            ImageBitmap.imageResource(R.drawable.scales),
                            Screen.PetitionListScreen.route,
                            navController
                        )

                        Spacer(modifier = Modifier.width(30.dp))

                        PersonalAccountMenuElement(
                            stringResource(id = R.string.my_messages),
                            "My Messages",
                            ImageBitmap.imageResource(R.drawable.message),
                            Screen.MessageListScreen.route,
                            navController
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        PersonalAccountMenuElement(
                            stringResource(id = R.string.my_profile),
                            "My profile",
                            ImageBitmap.imageResource(R.drawable.profile),
                            Screen.InspectorProfileScreen.route,
                            navController
                        )

                        Spacer(modifier = Modifier.width(30.dp))

                        PersonalAccountMenuElement(
                            stringResource(id = R.string.rating),
                            "Rating",
                            ImageBitmap.imageResource(R.drawable.top),
                            Screen.RatingScreen.route,
                            navController
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Personal Account",
    showBackground = true)
@Composable
fun PersonalAccountPreview() {
    GreenSignalTheme {
        PersonalAccountScreen(
            navController = rememberNavController(),
            state = PersonalAccountState(),
            {}
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Personal Account",
    showBackground = true,
    widthDp = 360,
    heightDp = 640)
@Composable
fun PersonalAccountHeightPreview() {
    GreenSignalTheme {
        PersonalAccountScreen(
            navController = rememberNavController(),
            state = PersonalAccountState(),
            {}
        )
    }
}