package com.example.greensignal.presentation.ui.screen

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.data.remote.dto.response.IncidentStatus
import com.example.greensignal.domain.model.response.Incident
import com.example.greensignal.presentation.event.CreateIncidentEvent
import com.example.greensignal.presentation.event.CreateIncidentReportEvent
import com.example.greensignal.presentation.event.IncidentListEvent
import com.example.greensignal.presentation.state.IncidentListState
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.element.IncidentListElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentListScreen(navController: NavController,
                       state: IncidentListState,
                       onEvent: (IncidentListEvent) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {

            HeaderRowBackElement(
                navController,
                Screen.PersonalAccountScreen.route,
                stringResource(id = R.string.my_inspector_profile_title)
            )

            TabRow(selectedTabIndex = state.selectedTabIndex) {

                state.tabOptions.forEachIndexed { index, title ->
                    Tab(
                        selected = state.selectedTabIndex == index,
                        onClick = { onEvent(IncidentListEvent.TabChanged(index)) },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }

            ExpandableItem(onEvent, state)

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
                IncidentList(navController, state, state.incidents)
            }
        }
    }
}

@Composable
fun ExpandableItem(onEvent: (IncidentListEvent) -> Unit, state: IncidentListState) {
    var expanded by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = expanded)
    val height by transition.animateDp { if (it) if(state.selectedTabIndex == 0) 175.dp else 225.dp else 50.dp }
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

                if (state.selectedTabIndex != 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        StatusDropDownMenu(onEvent)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                            .clickable(onClick = {
                                onEvent(IncidentListEvent.IsNerbyChanged(!state.isNerby))
                            })) {
                        Checkbox(
                            checked = state.isNerby,
                            onCheckedChange = { onEvent(IncidentListEvent.IsNerbyChanged(it)) }
                        )
                        Text(
                            text = stringResource(id = R.string.show_only_nerby),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KindDropDownMenu(onEvent: (IncidentListEvent) -> Unit) {
    val incidentKinds = IncidentKind.values()
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
                        onEvent(IncidentListEvent.IsSelectedIncidentKindChanged(null))
                    }
                )
                incidentKinds.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.title) },
                        onClick = {
                            selectedText = item.title
                            expanded = false
                            onEvent(IncidentListEvent.IsSelectedIncidentKindChanged(item))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusDropDownMenu(onEvent: (IncidentListEvent) -> Unit) {
    val incidentStatuses = IncidentStatus.values().filter { item ->
        item.index == 3 ||
        item.index == 4
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Все") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(id = R.string.incident_status), style = MaterialTheme.typography.labelLarge)

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
                        onEvent(IncidentListEvent.IsSelectedIncidentStatusChanged(null))
                    }
                )
                incidentStatuses.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.title) },
                        onClick = {
                            selectedText = item.title
                            expanded = false
                            onEvent(IncidentListEvent.IsSelectedIncidentStatusChanged(item))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun IncidentList(navController: NavController,
                         state: IncidentListState,
                         items: Flow<PagingData<Incident>>) {
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
private fun ListColumn(items: Flow<PagingData<Incident>>,
                        navController: NavController) {

    val incidents: LazyPagingItems<Incident> = items.collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(incidents.itemCount) {index ->
            IncidentListElement(incidents[index]!!, navController, true)
        }
        incidents.apply {
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
                    val errorState = incidents.loadState.refresh as LoadState.Error
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
                    val errorState = incidents.loadState.append as LoadState.Error
                    item {
                        //ErrorItem(errorState.error.localizedMessage)
                    }
                }
            }
        }
    }
}

@Composable
@Preview(name = "Incident List",
    showBackground = true)
fun IncidentListPreview() {
    GreenSignalTheme {
        IncidentListScreen(
            navController = rememberNavController(),
            state = IncidentListState(
                selectedTabIndex = 0,
                incidents = MutableStateFlow(PagingData.from(
                    listOf(
                    Incident(address = "Россия, Красноярский край, Красноярский кадастровый район, Кировский район, Апрельская улица, 6Б",
                        kind = IncidentKind.Dump,
                        status = IncidentStatus.Submitted,
                        createdAt = Date.from(Instant.now())),
                    Incident(address = "Россия, Красноярский край, Красноярский кадастровый район, Кировский район, Апрельская улица, 6Б",
                        kind = IncidentKind.Excavation,
                        status = IncidentStatus.Completed,
                        createdAt = Date.from(Instant.now())),
                    Incident(address = "Россия, Красноярский край, Красноярский кадастровый район, Кировский район, Апрельская улица, 6Б",
                        kind = IncidentKind.TreeCutting,
                        status = IncidentStatus.Closed,
                        createdAt = Date.from(Instant.now()))
                    )
                ))
            ),
            onEvent = {}
        )
    }
}

@Composable
@Preview(name = "Incident List",
    showBackground = true)
fun IncidentListTab1Preview() {
    GreenSignalTheme {
        IncidentListScreen(
            navController = rememberNavController(),
            state = IncidentListState(
                selectedTabIndex = 1,
                incidents = MutableStateFlow(PagingData.from(
                    listOf(
                    Incident(address = "Россия, Красноярский край, Красноярский кадастровый район, Кировский район, Апрельская улица, 6Б",
                        kind = IncidentKind.Dump,
                        status = IncidentStatus.Submitted,
                        createdAt = Date.from(Instant.now())),
                    Incident(address = "Россия, Красноярский край, Красноярский кадастровый район, Кировский район, Апрельская улица, 6Б",
                        kind = IncidentKind.Excavation,
                        status = IncidentStatus.Completed,
                        createdAt = Date.from(Instant.now())),
                    Incident(address = "Россия, Красноярский край, Красноярский кадастровый район, Кировский район, Апрельская улица, 6Б",
                        kind = IncidentKind.TreeCutting,
                        status = IncidentStatus.Closed,
                        createdAt = Date.from(Instant.now()))
                    )
                ))
            ),
            onEvent = {}
        )
    }
}