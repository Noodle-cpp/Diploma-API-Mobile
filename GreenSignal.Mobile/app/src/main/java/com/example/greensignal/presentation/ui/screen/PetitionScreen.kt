package com.example.greensignal.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.PetitionStatus
import com.example.greensignal.presentation.event.CreateIncidentEvent
import com.example.greensignal.presentation.event.IncidentEvent
import com.example.greensignal.presentation.event.PetitionEvent
import com.example.greensignal.presentation.state.PetitionState
import com.example.greensignal.presentation.ui.dialog.ConfirmationDialog
import com.example.greensignal.presentation.ui.element.ArgumentFieldElement
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.element.IncomeMessageElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme

@Composable
fun PetitionScreen(navController: NavController,
                   state: PetitionState,
                   onEvent: (PetitionEvent) -> Unit,
                   petitionId: String) {

    LaunchedEffect(key1 = true) {
        onEvent(PetitionEvent.GetPetition(petitionId))
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            HeaderRowBackElement(
                navController,
                Screen.PersonalAccountScreen.route,
                stringResource(id = R.string.my_inspector_profile_title)
            )

            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val showRemoveDialog =  remember { mutableStateOf(false) }
            val showCloseDialog =  remember { mutableStateOf(false) }

            if(showCloseDialog.value) {
                ClosePetition(show = showCloseDialog.value,
                                onDismiss = { showCloseDialog.value = false },
                                onConfirm = { showCloseDialog.value = false },
                                onEvent = onEvent,
                                navController = navController)
            }

            if(showRemoveDialog.value) {
                ConfirmationDialog(
                    show = showRemoveDialog.value,
                    onDismiss = { showRemoveDialog.value = false },
                    onConfirm = {
                        onEvent(PetitionEvent.RemovePetition)
                        navController.navigate(Screen.PetitionListScreen.route) {
                            popUpTo(Screen.PetitionListScreen.route) { inclusive = true }
                        }

                        showRemoveDialog.value = false
                    }
                )
            }

            Column {

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {

                    HeaderRowBackElement(
                        navController,
                        Screen.PetitionListScreen.route,
                        stringResource(id = R.string.my_petitions)
                    )

                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.petition) + " №${state.petition.serialNumber}",
                            style = MaterialTheme.typography.titleLarge,
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ArgumentFieldElement(
                            stringResource(id = R.string.department),
                            state.petition.department.name
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        state.petition.attributes.forEach {
                            val value = when (it.name) {
                                "OFFSITE_EVENTS" -> stringResource(id = R.string.is_offsite)
                                "MATERIALS" -> stringResource(id = R.string.is_materials)
                                "BRING_TO_JUSTICE" -> stringResource(id = R.string.is_bring_to_justice)
                                "EXAMINATION" -> stringResource(id = R.string.is_examination)
                                "DESCRIPTION" -> stringResource(id = R.string.desc_petition)
                                "REQUIREMENTS" -> stringResource(id = R.string.requirements_petition)
                                else -> ""
                            }

                            if (it.boolValue != null) {
                               if(it.boolValue) ArgumentFieldElement("", value)
                            } else {
                                val title = when (it.name) {
                                    "DESCRIPTION" -> stringResource(id = R.string.desc_petition)
                                    "REQUIREMENTS" -> stringResource(id = R.string.requirements_petition)
                                    else -> ""
                                }

                                ArgumentFieldElement(
                                    title,
                                    if (it.stringValue != null) it.stringValue.toString() else it.numberValue.toString()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        if(state.petition.status != PetitionStatus.Success && state.petition.status != PetitionStatus.Failed) {
                            Button(
                                onClick = {
                                    showCloseDialog.value = true
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.close_petition),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .clickable(onClick = {
                                    navController.navigate(Screen.CreatePetitionScreen.route + "/petition/${state.petition.id}")
                                })
                        ) {
                            Icon(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(25.dp),
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Exit",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(text = stringResource(id = R.string.attach_as_parent),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.clickable(onClick = {
                                    navController.navigate(Screen.CreatePetitionScreen.route + "/petition/${state.petition.id}")
                                })
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if(state.petition.status != PetitionStatus.Success && state.petition.status != PetitionStatus.Failed) {
                            Button(
                                onClick = {
                                    showRemoveDialog.value = true
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Gray,
                                    disabledContentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.delete),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = stringResource(id = R.string.attached_messages),
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    val columnHeight = state.petition.receiveMessages.size * 170

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(columnHeight.dp)
                    ) {
                        items(state.petition.receiveMessages.size) { index ->
                            IncomeMessageElement(
                                state.petition.receiveMessages[index],
                                navController
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClosePetition(show: Boolean,
                  onDismiss: () -> Unit,
                  onConfirm: () -> Unit,
                  onEvent: (PetitionEvent) -> Unit,
                  navController: NavController) {
    val radioOptions = listOf(PetitionStatus.Success, PetitionStatus.Failed)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    if (show) {
        Dialog(
            onDismissRequest = {
                onDismiss()
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ElevatedCard(modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .shadow(7.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(Color.White),
                shape = RoundedCornerShape(20.dp),
            ) {
                Box(modifier = Modifier) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.Center)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(id = R.string.close_petition),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )

                            Button(
                                onClick = { onDismiss() },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                            ) {
                                Text("X")
                            }
                        }


                        Spacer(modifier = Modifier.height(20.dp))

                        radioOptions.forEach { status ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (status == selectedOption),
                                        onClick = {
                                            onOptionSelected(status)
                                        }
                                    )
                                    .padding(horizontal = 16.dp)
                            ) {
                                RadioButton(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    selected = (status == selectedOption),
                                    onClick = { onOptionSelected(status) }
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = status.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                onEvent(PetitionEvent.ClosePetition(selectedOption))
                                onConfirm()
//                                navController.navigate(Screen.PetitionListScreen.route) {
//                                    popUpTo(Screen.PetitionListScreen.route) { inclusive = true }
//                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.close_petition),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PetitionScreenPreview() {
    GreenSignalTheme {
        PetitionScreen(navController = rememberNavController(), state = PetitionState(), onEvent = {}, petitionId = "")
    }
}