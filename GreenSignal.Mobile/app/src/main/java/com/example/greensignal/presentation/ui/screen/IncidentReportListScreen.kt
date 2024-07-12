package com.example.greensignal.presentation.ui.screen

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.IncidentReportKind
import com.example.greensignal.data.remote.dto.response.IncidentReportStatus
import com.example.greensignal.domain.model.response.IncidentReport
import com.example.greensignal.presentation.event.IncidentReportListEvent
import com.example.greensignal.presentation.state.IncidentReportListState
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.element.IncidentReportListElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentReportListScreen(navController: NavController,
                             state: IncidentReportListState,
                             onEvent: (IncidentReportListEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {

            HeaderRowBackElement(
                navController,
                Screen.PersonalAccountScreen.route,
                stringResource(id = R.string.my_inspector_profile_title),
                stringResource(id = R.string.create_incident_report),
                Icons.Default.Add,
                Screen.CreateIncidentReportScreen.withArgs(null)
            )

            ExpandableItem(onEvent)

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                IncidentReportList(navController, state, state.incidentReports)
            }
        }
    }
}

@Composable
fun ExpandableItem(onEvent: (IncidentReportListEvent) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = expanded)
    val height by transition.animateDp { if (it) 225.dp else 50.dp }
    val angle: Float by transition.animateFloat { if (it) 180F else 0F }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(10.dp)
            .clickable { expanded = !expanded }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Фильтры", modifier = Modifier.align(Alignment.CenterStart))
            Icon(imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "expand",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .rotate(angle)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (expanded) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    KindDropDownMenu(onEvent)
                }

                Spacer(modifier = Modifier.height(10.dp))


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    StatusDropDownMenu(onEvent)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KindDropDownMenu(onEvent: (IncidentReportListEvent) -> Unit) {
    val incidentReportKinds = IncidentReportKind.values()
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Все") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.incident_kind),
            style = MaterialTheme.typography.labelLarge
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Все") },
                    onClick = {
                        selectedText = "Все"
                        expanded = false
                        onEvent(IncidentReportListEvent.IsSelectedIncidentReportKindChanged(null))
                    }
                )
                incidentReportKinds.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.title) },
                        onClick = {
                            selectedText = item.title
                            expanded = false
                            onEvent(IncidentReportListEvent.IsSelectedIncidentReportKindChanged(item))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusDropDownMenu(onEvent: (IncidentReportListEvent) -> Unit) {
    val incidentReportStatuses = IncidentReportStatus.values().filter {
        it != IncidentReportStatus.Archived
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Все") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(id = R.string.incident_report_status), style = MaterialTheme.typography.labelLarge)

        ExposedDropdownMenuBox(
            expanded = expanded,
            modifier = Modifier.fillMaxWidth(),
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Все") },
                    onClick = {
                        selectedText = "Все"
                        expanded = false
                        onEvent(IncidentReportListEvent.IsSelectedIncidentReportStatusChanged(null))
                    }
                )
                incidentReportStatuses.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.title) },
                        onClick = {
                            selectedText = item.title
                            expanded = false
                            onEvent(IncidentReportListEvent.IsSelectedIncidentReportStatusChanged(item))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun IncidentReportList(navController: NavController,
                               state: IncidentReportListState,
                               items: Flow<PagingData<IncidentReport>>
) {
    Box {
        if(state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        } else {
            ListColumn(items, navController)
        }
    }
}

@Composable
private fun ListColumn(items: Flow<PagingData<IncidentReport>>,
                       navController: NavController
) {
    val incidentReports: LazyPagingItems<IncidentReport> = items.collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(incidentReports.itemCount) {index ->
            IncidentReportListElement(incidentReports[index]!!, navController)
        }
        incidentReports.apply {
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
                    val errorState = incidentReports.loadState.refresh as LoadState.Error
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
                    val errorState = incidentReports.loadState.append as LoadState.Error
                    item {
                        //ErrorItem(errorState.error.localizedMessage)
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun IncidentReportListScreenPreview() {
    GreenSignalTheme {
        IncidentReportListScreen(navController = rememberNavController(), state = IncidentReportListState(), onEvent = {})
    }
}