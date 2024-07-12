package com.example.greensignal.presentation.ui.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.greensignal.R
import com.example.greensignal.domain.model.response.Incident
import com.example.greensignal.presentation.event.HomeEvent
import com.example.greensignal.presentation.state.HomeState
import com.example.greensignal.presentation.ui.element.IncidentListElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import kotlinx.coroutines.flow.Flow


@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    navController: NavController,
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {

        LaunchedEffect(true) {
//            onEvent(HomeEvent.GetIncidentStatistic)
//            onEvent(HomeEvent.GetIncidentReportStatistic)
            onEvent(HomeEvent.CheckToken)
        }

        Column {
            HeaderRow(navController, state)

            Spacer(modifier = Modifier.height(30.dp))

            CardStatistic(navController, state, onEvent)

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = stringResource(id = R.string.my_incidents),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                IncidentList(navController, state, state.incidents, onEvent)
            }
        }
    }
}

@Composable
fun HeaderRow(navController: NavController, state: HomeState) {
    Box(modifier = Modifier
        .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd)
    {
        if(!state.isAuthorized) {
            Row(modifier = Modifier
                .clickable {
                    navController.navigate(Screen.AuthenticationScreen.route)
                }) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.authorize),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Account",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        else {
            Row(modifier = Modifier
                .clickable {
                    navController.navigate(Screen.PersonalAccountScreen.route)
                }) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.account),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Account",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CardStatistic(navController: NavController, state: HomeState, onEvent: (HomeEvent) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(15.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(20.dp),
    ) {

        Column(modifier = Modifier
            .padding(15.dp)
            .align(Alignment.CenterHorizontally)) {
            if(state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(10.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            else if(state.error != null) {
                Column(modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)) {
                    Icon(
                        modifier = Modifier
                            .height(50.dp)
                            .width(50.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                onEvent(HomeEvent.GetIncidentStatistic)
                                onEvent(HomeEvent.GetIncidentReportStatistic)
                            },
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Card Refresh",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = state.error.toString(),
                        modifier = Modifier,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error)
                }
            }
            else {
                Row(modifier = Modifier) {
                    Column(modifier = Modifier) {
                        CardText(
                            text = state.countOfIncidents.toString(),
                            fontWeight = FontWeight.ExtraBold
                        )
                        CardText(
                            text = state.countOfCompletedIncidents.toString(),
                            fontWeight = FontWeight.ExtraBold
                        )
                        CardText(
                            text = state.countOfIncidentsInWork.toString(),
                            fontWeight = FontWeight.ExtraBold
                        )
                        CardText(
                            text = state.countOfSentIncidentReports.toString(),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(modifier = Modifier) {
                        CardText(text = stringResource(id = R.string.incident_list_statistic))
                        CardText(text = stringResource(id = R.string.closed_incident_list_statistic))
                        CardText(text = stringResource(id = R.string.incident_list_in_work_statistic))
                        CardText(text = stringResource(id = R.string.send_incident_list_statistic))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { navController.navigate(Screen.CreateIncidentScreen.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(id = R.string.create_incident),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun CardText(text: String, fontWeight: FontWeight = FontWeight.Normal) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = fontWeight,
    )

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun IncidentList(navController: NavController,
                         state: HomeState,
                         items: Flow<PagingData<Incident>>,
                         onEvent: (HomeEvent) -> Unit) {
    Box {
        if(state.isCitizenAuthorized) {
            ListColumn(items, navController, onEvent)
        }
    }
}

@Composable
private fun ListColumn(items: Flow<PagingData<Incident>>,
                       navController: NavController,
                       onEvent: (HomeEvent) -> Unit) {

    val incidents: LazyPagingItems<Incident> = items.collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(incidents.itemCount) {index ->
            IncidentListElement(incidents[index]!!, navController, false)
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

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Home",
    showBackground = true)
@Composable
fun HomeScreenPreview() {
    GreenSignalTheme {
        HomeScreen(
            navController = rememberNavController(),
            state = HomeState(),
            onEvent = {}
        )
    }
}
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Home Error",
    showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    GreenSignalTheme {
        HomeScreen(
            navController = rememberNavController(),
            state = HomeState(error = "Ошибка. Нет подключения к интернету"),
            onEvent = {}
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Home Loading",
    showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    GreenSignalTheme {
        HomeScreen(
            navController = rememberNavController(),
            state = HomeState(isLoading = true),
            onEvent = {}
        )
    }
}