package com.example.greensignal.presentation.ui.screen

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.domain.model.response.Session
import com.example.greensignal.presentation.event.RatingEvent
import com.example.greensignal.presentation.event.SessionListEvent
import com.example.greensignal.presentation.state.RatingState
import com.example.greensignal.presentation.state.SessionListState
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.element.RatingCardElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SessionListScreen(navController: NavController,
                      state: SessionListState,
                      onEvent: (SessionListEvent) -> Unit ) {

    if (state.isLoading)
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HeaderRowBackElement(
                navController,
                Screen.InspectorProfileScreen.route,
                stringResource(id = R.string.my_inspector_profile_title)
            )
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {

                HeaderRowBackElement(
                    navController = navController,
                    Screen.InspectorProfileScreen.route,
                    stringResource(id = R.string.my_inspector_profile_title)
                )

                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = stringResource(id = R.string.session_list),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(state.sessions) { model ->
                            SessionRow(model, onEvent)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionRow(session: Session, onEvent: (SessionListEvent) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth())
    {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(40.dp),
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "remove",
            tint = MaterialTheme.colorScheme.primary
        )

        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = session.deviceName,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = session.ip,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = SimpleDateFormat("d MMMM 'в' HH:mm", Locale("ru")).format(session.cretedAt),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(50.dp)
                .clickable(onClick = {
                    onEvent(SessionListEvent.RemoveSession(session.id))
                }),
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "remove",
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SessionListScreenPreview() {
    GreenSignalTheme {
        SessionListScreen(rememberNavController(), SessionListState(mutableListOf(Session(
            id = "123",
            deviceName = "RMX3085",
            ip ="192.168.0.1",
            cretedAt = Date()
        ))), {})
    }
}