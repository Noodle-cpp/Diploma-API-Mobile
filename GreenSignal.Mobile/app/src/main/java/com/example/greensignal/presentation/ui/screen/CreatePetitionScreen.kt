package com.example.greensignal.presentation.ui.screen

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.greensignal.R
import com.example.greensignal.domain.model.response.Department
import com.example.greensignal.presentation.event.CreatePetitionEvent
import com.example.greensignal.presentation.state.CreatePetitionState
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun CreatePetitionScreen(navController: NavController,
                         state: CreatePetitionState,
                         onEvent: (CreatePetitionEvent) -> Unit,
                         incidentReportId: String? = null,
                         petitionId: String? = null) {

    LaunchedEffect(key1 = true) {
        onEvent(CreatePetitionEvent.IncidentReportChanged(incidentReportId))
        onEvent(CreatePetitionEvent.PetitionChanged(petitionId))
    }
    
    LaunchedEffect(key1 = state.petitionId) {
        if(state.petitionId != null)
            navController.navigate(Screen.PetitionScreen.withArgs(state.petitionId)) {
                if(state.incidentReportId != null)
                    popUpTo(Screen.IncidentReportScreen.withArgs(state.incidentReportId)) { inclusive = true }
                else if (state.parentPetitionId != null)
                    popUpTo(Screen.PetitionScreen.withArgs(state.petitionId)) { inclusive = true }
            }
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
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
            ) {

                HeaderRowBackElement(
                    navController,
                    if(incidentReportId != null) Screen.IncidentReportScreen.withArgs(incidentReportId) else "",
                    if(incidentReportId != null) stringResource(id = R.string.incident_report) else stringResource(id = R.string.petition)
                )

                Column(modifier = Modifier
                    .padding(10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.create_petition),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ExpandableItem(onEvent, state, state.departments)

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = {
                            onEvent(CreatePetitionEvent.DescriptionChanged(it))
                        },
                        label = { Text(stringResource(id = R.string.desc_petition)) },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                onEvent(CreatePetitionEvent.IsOffsiteEventsChanged(!state.isOffsiteEvents))
                            })) {
                        Checkbox(
                            checked = state.isOffsiteEvents,
                            onCheckedChange = { onEvent(CreatePetitionEvent.IsOffsiteEventsChanged(it)) }
                        )
                        Text(
                            text = stringResource(id = R.string.is_offsite),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                onEvent(CreatePetitionEvent.IsMaterialsEventsChanged(!state.isMaterials))
                            })) {
                        Checkbox(
                            checked = state.isMaterials,
                            onCheckedChange = { onEvent(CreatePetitionEvent.IsMaterialsEventsChanged(it)) }
                        )
                        Text(
                            text = stringResource(id = R.string.is_materials),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                onEvent(CreatePetitionEvent.IsBringToJusticeEventsChanged(!state.isBringToJustice))
                            })) {
                        Checkbox(
                            checked = state.isBringToJustice,
                            onCheckedChange = { onEvent(CreatePetitionEvent.IsBringToJusticeEventsChanged(it)) }
                        )
                        Text(
                            text = stringResource(id = R.string.is_bring_to_justice),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                onEvent(CreatePetitionEvent.IsExaminationEventsChanged(!state.isExamination))
                            })) {
                        Checkbox(
                            checked = state.isExamination,
                            onCheckedChange = { onEvent(CreatePetitionEvent.IsExaminationEventsChanged(it)) }
                        )
                        Text(
                            text = stringResource(id = R.string.is_examination),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.requirements,
                        onValueChange = {
                            onEvent(CreatePetitionEvent.RequirementsChanged(it))
                        },
                        label = { Text(stringResource(id = R.string.requirements_petition)) },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            onEvent(CreatePetitionEvent.CreatePetition)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = stringResource(id = R.string.create_petition),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

@Composable
fun ExpandableItem(onEvent: (CreatePetitionEvent) -> Unit, state: CreatePetitionState, items: Flow<PagingData<Department>>) {
    var expanded by remember { mutableStateOf(false) }

    val departments: LazyPagingItems<Department> = items.collectAsLazyPagingItems()

    val transition = updateTransition(targetState = expanded)
    val height by transition.animateDp { if (it)  500.dp else 100.dp }
    val angle: Float by transition.animateFloat { if (it) 0F else -90F }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable {
                if (!expanded) onEvent(CreatePetitionEvent.GetDepartmentList)
                expanded = !expanded
            }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(text = "Комитет", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = if (state.selectedDepartment != null) state.selectedDepartment.name
                    else stringResource(
                        id = R.string.attach_department
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "expand",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .rotate(angle)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (expanded) {
            Column {
                Column(modifier = Modifier.clickable(onClick = {
                    onEvent(CreatePetitionEvent.GetDepartment(null))
                    expanded = !expanded
                })) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.dp,
                        color = Color.LightGray
                    )
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(departments.itemCount) { index ->
                        Column(modifier = Modifier.clickable(onClick = {
                            onEvent(CreatePetitionEvent.GetDepartment(departments[index]!!.id))
                            expanded = !expanded
                        })) {

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column {

                                    Text(
                                        text = departments[index]!!.aliasNames,
                                        style = MaterialTheme.typography.titleMedium,
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = departments[index]!!.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
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
                    departments.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier.fillParentMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(10.dp),
                                            color = MaterialTheme.colorScheme.secondary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            }
                            loadState.refresh is LoadState.Error -> {
                                val errorState = departments.loadState.refresh as LoadState.Error
                                item {
                                    //ErrorItem(errorState.error.localizedMessage)
                                }
                            }
                            loadState.append is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(10.dp),
                                            color = MaterialTheme.colorScheme.secondary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            }
                            loadState.append is LoadState.Error -> {
                                val errorState = departments.loadState.append as LoadState.Error
                                item {
                                    //ErrorItem(errorState.error.localizedMessage)
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = Color.LightGray
        )
    }
}

@Composable
@Preview(showBackground = true)
fun CreatePetitionScreenPreview() {
    GreenSignalTheme {
        CreatePetitionScreen(navController = rememberNavController(),
                            state = CreatePetitionState(),
                            incidentReportId = null,
                            petitionId = null,
                            onEvent = {})
    }
}