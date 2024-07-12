package com.example.greensignal.presentation.ui.screen

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.ScoreType
import com.example.greensignal.domain.model.response.Inspector
import com.example.greensignal.domain.model.response.Rating
import com.example.greensignal.domain.model.response.Score
import com.example.greensignal.presentation.event.AuthenticationEvent
import com.example.greensignal.presentation.event.RatingEvent
import com.example.greensignal.presentation.state.RatingState
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.element.RatingCardElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
fun RatingScreen(navController: NavController,
                 state: RatingState,
                 onEvent: (RatingEvent) -> Unit ) {

    if (state.isLoading)
        Box(
            modifier = Modifier
                .fillMaxSize()
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
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {

                HeaderRowBackElement(
                    navController = navController,
                    Screen.PersonalAccountScreen.route,
                    stringResource(id = R.string.my_inspector_profile_title)
                )

                Column(modifier = Modifier.padding(15.dp)) {

                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = stringResource(id = R.string.rating),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
                    ) {
                        TabRow(selectedTabIndex = state.selectedTabIndex)
                        {
                            state.tabOptions.forEachIndexed { index, title ->
                                Tab(
                                    selected = state.selectedTabIndex == index,
                                    onClick = { onEvent(RatingEvent.TabChanged(index)) },
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }
                    }

                    RatingCardElement(
                        topThreeList = state.rating,
                        currentInspector = state.currentInspectorScore
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(state.scoreHistory) { model ->
                            ScoreRow(model)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreRow(score: Score) {
    Row {
        val outputDateFormat = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.getDefault())
        val offsetDateTime = OffsetDateTime.parse(score.date)
        val date = offsetDateTime.format(outputDateFormat)


        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = date,
            textAlign = TextAlign.Start,
            modifier = Modifier.width(70.dp),
            color = Color.Gray
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = score.type.title,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        Text(
            text = score.score.toString(),
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.ExtraBold,
            color = if(score.score < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Account",
    showBackground = true)
@Composable
fun RatingScreenPreview() {
    GreenSignalTheme {
        RatingScreen(
            navController = rememberNavController(),
            state = RatingState(rating =
                mutableListOf(
                    Rating(place = 1, inspector = Inspector(fio = "Шибанова Валентина Сергеевна"), totalScore = 1000),
                    Rating(place = 2, inspector = Inspector(fio = "Иванов Иван Иванович"), totalScore = 300),
                    Rating(place = 3, inspector = Inspector(fio = "Петров Петр Петрович"), totalScore = 250)
                ),
                currentInspectorScore =  Rating(place = 24, inspector = Inspector(fio = "Константинов Константин Константинович"), totalScore = 5),
                scoreHistory = mutableListOf(Score(date = "2024-01-13T07:00:00Z", score = 150, type = ScoreType.TakeIncident),
                                                Score(date = "2024-01-13T07:00:00Z", score = -50, type = ScoreType.OverdueIncident))
            ),
            onEvent = {}
        )
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Account",
    showBackground = true)
@Composable
fun RatingScreenLoadingPreview() {
    GreenSignalTheme {
        RatingScreen(
            navController = rememberNavController(),
            state = RatingState(isLoading = true),
            onEvent = {}
        )
    }
}