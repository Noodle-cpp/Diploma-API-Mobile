package com.example.greensignal.presentation.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.example.greensignal.domain.model.response.toRating
import com.example.greensignal.domain.model.response.toScore
import com.example.greensignal.domain.repository.InspectorRepository
import com.example.greensignal.presentation.event.RatingEvent
import com.example.greensignal.presentation.state.RatingState
import com.example.greensignal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val inspectorRepository: InspectorRepository,
    private val prefs: SharedPreferences,
    ): ViewModel() {
    var state by mutableStateOf(RatingState())
        private set

    private val validationEventChannel = Channel<RatingViewModel.ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val utcStartDateTime = YearMonth.from(LocalDateTime.now()).atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC)
        val utcEndDateTime = YearMonth.from(LocalDateTime.now()).atEndOfMonth().atTime(23, 59, 59).atOffset(ZoneOffset.UTC)

        state = state.copy(startDate = outputFormatter.format(utcStartDateTime),
            endDate = outputFormatter.format(utcEndDateTime))

        onGetRating()
        onGetScoreHistory()
        onGetInspectorRating()
    }

    fun onEvent(event: RatingEvent) {
        when (event) {

            RatingEvent.GetRating -> {
                onGetRating()
            }

            RatingEvent.GetScoreHistory ->
                onGetScoreHistory()

            RatingEvent.GetInspectorRating -> {
                onGetInspectorRating()
            }

            is RatingEvent.TabChanged -> {
                state = state.copy(selectedTabIndex = event.index)
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

                when(state.selectedTabIndex) {
                    0 -> {
                        val utcStartDateTime = YearMonth.from(LocalDateTime.now()).atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC)
                        val utcEndDateTime = YearMonth.from(LocalDateTime.now()).atEndOfMonth().atTime(23, 59, 59).atOffset(ZoneOffset.UTC)

                        state = state.copy(startDate = outputFormatter.format(utcStartDateTime),
                            endDate = outputFormatter.format(utcEndDateTime))
                    }
                    1 -> {
                        val utcStartDateTime = OffsetDateTime.of(OffsetDateTime.now().year, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
                        val utcEndDateTime = OffsetDateTime.of(OffsetDateTime.now().year, 12, 31, 23, 59, 59, 999999999, ZoneOffset.UTC)

                        state = state.copy(startDate = outputFormatter.format(utcStartDateTime),
                            endDate = outputFormatter.format(utcEndDateTime))
                    }
                    2 -> {
                        state = state.copy(startDate = null,
                                            endDate = null)
                    }
                }

                onGetRating()
                onGetScoreHistory()
                onGetInspectorRating()
            }
        }
    }

    private fun onGetRating() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            val token = prefs.getString("inspector-jwt-token", null)

            if(token != null) {

                when (val response =
                    inspectorRepository.getRating(state.startDate, state.endDate, token)) {
                    is Resource.Success -> {

                        state = state.copy(
                            isLoading = false,
                            error = null,
                        )

                        state = state.copy(rating = response.data!!.map { x -> x.toRating() }.toMutableList())

                        if(state.rating.isEmpty()) state.rating.add(state.currentInspectorScore)

                        validationEventChannel.send(RatingViewModel.ValidationEvent.Success)
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = response.message!!,
                        )
                    }
                }
            } else {
                state = state.copy(
                    isLoading = false,
                )
            }
        }
    }

    private fun onGetInspectorRating() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            val token = prefs.getString("inspector-jwt-token", null)

            if(token != null) {
                var jwt: JWT = JWT(token)
                var inspectorId: String? = jwt.getClaim("id").asString()

                when (val response =
                    inspectorRepository.getInspectorRating(inspectorId!!, state.startDate, state.endDate, token)) {
                    is Resource.Success -> {

                        state = state.copy(
                            isLoading = false,
                            error = null,
                        )

                        state = state.copy(currentInspectorScore = response.data!!.toRating())

                        validationEventChannel.send(RatingViewModel.ValidationEvent.Success)
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = response.message!!,
                        )
                    }
                }
            } else {
                state = state.copy(
                    isLoading = false,
                )
            }
        }
    }

    private fun onGetScoreHistory() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            val token = prefs.getString("inspector-jwt-token", null)

            if(token != null) {
                var jwt: JWT = JWT(token)
                var inspectorId: String? = jwt.getClaim("id").asString()

                when (val response =
                    inspectorRepository.getScoreHistory(inspectorId!!, state.startDate, state.endDate, state.page, state.perPage, token)) {
                    is Resource.Success -> {

                        state = state.copy(
                            isLoading = false,
                            error = null,
                        )

                        state = state.copy(scoreHistory = response.data!!.map { x -> x.toScore() }.toMutableList())

                        validationEventChannel.send(RatingViewModel.ValidationEvent.Success)
                    }

                    is Resource.Error -> {
                        state = state.copy(
                            isLoading = false,
                            error = response.message!!,
                        )
                    }
                }
            } else {
                state = state.copy(
                    isLoading = false,
                )
            }
        }
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }
}