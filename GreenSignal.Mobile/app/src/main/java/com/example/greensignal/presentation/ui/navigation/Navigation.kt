package com.example.greensignal.presentation.ui.navigation

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.greensignal.presentation.ui.screen.InspectorProfileScreen
import com.example.greensignal.presentation.ui.screen.CreateIncidentScreen
import com.example.greensignal.presentation.ui.screen.HomeScreen
import com.example.greensignal.presentation.ui.screen.AuthenticationScreen
import com.example.greensignal.presentation.ui.screen.CreateIncidentReportScreen
import com.example.greensignal.presentation.ui.screen.CreatePetitionScreen
import com.example.greensignal.presentation.ui.screen.IncidentListScreen
import com.example.greensignal.presentation.ui.screen.IncidentReportListScreen
import com.example.greensignal.presentation.ui.screen.IncidentReportScreen
import com.example.greensignal.presentation.ui.screen.IncidentScreen
import com.example.greensignal.presentation.ui.screen.MessageListScreen
import com.example.greensignal.presentation.ui.screen.MessageScreen
import com.example.greensignal.presentation.ui.screen.PersonalAccountScreen
import com.example.greensignal.presentation.ui.screen.PetitionListScreen
import com.example.greensignal.presentation.ui.screen.PetitionScreen
import com.example.greensignal.presentation.ui.screen.RatingScreen
import com.example.greensignal.presentation.ui.screen.SessionListScreen
import com.example.greensignal.presentation.ui.screen.UpdateIncidentReportScreen
import com.example.greensignal.presentation.ui.screen.UpdateInspectorScreen
import com.example.greensignal.presentation.viewmodel.InspectorProfileViewModel
import com.example.greensignal.presentation.viewmodel.CreateIncidentViewModel
import com.example.greensignal.presentation.viewmodel.HomeViewModel
import com.example.greensignal.presentation.viewmodel.AuthenticationViewModel
import com.example.greensignal.presentation.viewmodel.CreateIncidentReportViewModel
import com.example.greensignal.presentation.viewmodel.CreatePetitionViewModel
import com.example.greensignal.presentation.viewmodel.IncidentListViewModel
import com.example.greensignal.presentation.viewmodel.IncidentReportListViewModel
import com.example.greensignal.presentation.viewmodel.IncidentReportViewModel
import com.example.greensignal.presentation.viewmodel.IncidentViewModel
import com.example.greensignal.presentation.viewmodel.MessageListViewModel
import com.example.greensignal.presentation.viewmodel.MessageViewModel
import com.example.greensignal.presentation.viewmodel.PersonalAccountViewModel
import com.example.greensignal.presentation.viewmodel.PetitionListViewModel
import com.example.greensignal.presentation.viewmodel.PetitionViewModel
import com.example.greensignal.presentation.viewmodel.RatingViewModel
import com.example.greensignal.presentation.viewmodel.SessionListViewModel
import com.example.greensignal.presentation.viewmodel.UpdateIncidentReportViewModel
import com.example.greensignal.presentation.viewmodel.UpdateInspectorViewModel

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(route = Screen.HomeScreen.route) {
            val homeViewModel = hiltViewModel<HomeViewModel>()

            HomeScreen(
                navController = navController,
                state = homeViewModel.state,
                onEvent = homeViewModel::onEvent
            )
        }

        composable(route = Screen.AuthenticationScreen.route) {
            val authenticationViewModel = hiltViewModel<AuthenticationViewModel>()

            AuthenticationScreen(
                navController = navController,
                state = authenticationViewModel.state,
                onEvent = authenticationViewModel::onEvent
            )
        }

        composable(route = Screen.CreateIncidentScreen.route) {
            val createIncidentViewModel = hiltViewModel<CreateIncidentViewModel>()

            CreateIncidentScreen(
                navController = navController,
                state = createIncidentViewModel.state,
                onEvent = createIncidentViewModel::onEvent
            )
        }

        composable(route = Screen.InspectorProfileScreen.route) {
            val inspectorProfileViewModel = hiltViewModel<InspectorProfileViewModel>()

            InspectorProfileScreen(
                navController = navController,
                state = inspectorProfileViewModel.state,
                onEvent = inspectorProfileViewModel::onEvent
            )
        }

        composable(route = Screen.PersonalAccountScreen.route) {
            val personalAccountViewModel = hiltViewModel<PersonalAccountViewModel>()

            PersonalAccountScreen(
                navController = navController,
                state = personalAccountViewModel.state,
                onEvent = personalAccountViewModel::onEvent
            )
        }

        composable(route = Screen.RatingScreen.route) {
            val ratingViewModel = hiltViewModel<RatingViewModel>()

            RatingScreen(
                navController = navController,
                state = ratingViewModel.state,
                onEvent = ratingViewModel::onEvent
            )
        }

        composable(route = Screen.IncidentListScreen.route) {
            val incidentListViewModel = hiltViewModel<IncidentListViewModel>()

            IncidentListScreen(
                navController = navController,
                state = incidentListViewModel.state,
                onEvent = incidentListViewModel::onEvent
            )
        }

        composable(
            route = Screen.IncidentScreen.route + "/{incidentId}",
            arguments = listOf(
                navArgument("incidentId") {
                    type = NavType.StringType
                    defaultValue = "123"
                    nullable = true
                },
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "myapp://green_signal/${Screen.IncidentScreen.route}/{incidentId}" })) { entry ->
            val incidentViewModel = hiltViewModel<IncidentViewModel>()

            IncidentScreen(
                incidentId = entry.arguments?.getString("incidentId")!!,
                navController = navController,
                state = incidentViewModel.state,
                onEvent = incidentViewModel::onEvent,
            )
        }

        composable(route = Screen.MessageListScreen.route) {
            val messageListViewModel = hiltViewModel<MessageListViewModel>()

            MessageListScreen(
                navController = navController,
                state = messageListViewModel.state,
                onEvent = messageListViewModel::onEvent
            )
        }

        composable(route = Screen.MessageScreen.route + "/{messageId}",
            arguments = listOf(
                navArgument("messageId") {
                    type = NavType.StringType
                    defaultValue = "123"
                    nullable = true
                }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "myapp://green_signal/${Screen.MessageScreen.route}/{messageId}" })) { entry ->
            val messageViewModel = hiltViewModel<MessageViewModel>()

            MessageScreen(
                messageId = entry.arguments?.getString("messageId")!!,
                navController = navController,
                state = messageViewModel.state,
                onEvent = messageViewModel::onEvent,
            )
        }

        composable(route = Screen.UpdateInspectorScreen.route) {
            val updateInspectorViewModel = hiltViewModel<UpdateInspectorViewModel>()

            UpdateInspectorScreen(
                navController = navController,
                state = updateInspectorViewModel.state,
                onEvent = updateInspectorViewModel::onEvent
            )
        }

        composable(route = Screen.CreateIncidentReportScreen.route + "/{incidentId}",
                    arguments = listOf(
                        navArgument("incidentId") {
                            type = NavType.StringType
                            defaultValue = null
                            nullable = true
                        }
                    ),
                    deepLinks = listOf(navDeepLink { uriPattern = "myapp://green_signal/${Screen.CreateIncidentReportScreen.route}/{incidentId}" })) { entry ->
            val createIncidentReportViewModel = hiltViewModel<CreateIncidentReportViewModel>()

            CreateIncidentReportScreen(
                incidentId = entry.arguments?.getString("incidentId"),
                navController = navController,
                state = createIncidentReportViewModel.state,
                onEvent = createIncidentReportViewModel::onEvent
            )
        }

        composable(route = Screen.IncidentReportListScreen.route) {
            val incidentReportListViewModel = hiltViewModel<IncidentReportListViewModel>()

            IncidentReportListScreen(
                navController = navController,
                state = incidentReportListViewModel.state,
                onEvent = incidentReportListViewModel::onEvent
            )
        }

        composable(route = Screen.SessionListScreen.route) {
            val sessionListViewModel = hiltViewModel<SessionListViewModel>()

            SessionListScreen(
                navController = navController,
                state = sessionListViewModel.state,
                onEvent = sessionListViewModel::onEvent
            )
        }

        composable(
            route = Screen.UpdateIncidentReportScreen.route + "/{incidentReportId}",
            arguments = listOf(
                navArgument("incidentReportId") {
                    type = NavType.StringType
                    defaultValue = "123"
                    nullable = true
                },
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "myapp://green_signal/${Screen.UpdateIncidentReportScreen.route}/{incidentReportId}" })) { entry ->
            val updateIncidentReportViewModel = hiltViewModel<UpdateIncidentReportViewModel>()

            UpdateIncidentReportScreen(
                incidentReportId = entry.arguments?.getString("incidentReportId")!!,
                navController = navController,
                state = updateIncidentReportViewModel.state,
                onEvent = updateIncidentReportViewModel::onEvent,
            )
        }

        composable(
            route = Screen.IncidentReportScreen.route + "/{incidentReportId}",
            arguments = listOf(
                navArgument("incidentReportId") {
                    type = NavType.StringType
                    defaultValue = "123"
                    nullable = true
                },
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "myapp://green_signal/${Screen.IncidentReportScreen.route}/{incidentReportId}" })) { entry ->
            val incidentReportViewModel = hiltViewModel<IncidentReportViewModel>()

            IncidentReportScreen(
                incidentReportId = entry.arguments?.getString("incidentReportId")!!,
                navController = navController,
                state = incidentReportViewModel.state,
                onEvent = incidentReportViewModel::onEvent,
            )
        }

        composable(
            route = Screen.CreatePetitionScreen.route + "/incidentReport/{incidentReportId}",
            arguments = listOf(
                navArgument("incidentReportId") {
                    type = NavType.StringType
                    defaultValue = "123"
                    nullable = true
                },
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "myapp://green_signal/${Screen.CreatePetitionScreen.route}/incidentReport/{incidentReportId}" })) { entry ->
            val createPetitionViewModel = hiltViewModel<CreatePetitionViewModel>()

            CreatePetitionScreen(
                incidentReportId = entry.arguments?.getString("incidentReportId")!!,
                petitionId = null,
                navController = navController,
                state = createPetitionViewModel.state,
                onEvent = createPetitionViewModel::onEvent,
            )
        }

        composable(
            route = Screen.CreatePetitionScreen.route + "/petition/{petitionId}",
            arguments = listOf(
                navArgument("petitionId") {
                    type = NavType.StringType
                    defaultValue = "123"
                    nullable = true
                },
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "myapp://green_signal/${Screen.CreatePetitionScreen.route}/petition/{petitionId}" })) { entry ->
            val createPetitionViewModel = hiltViewModel<CreatePetitionViewModel>()

            CreatePetitionScreen(
                petitionId = entry.arguments?.getString("petitionId")!!,
                incidentReportId = null,
                navController = navController,
                state = createPetitionViewModel.state,
                onEvent = createPetitionViewModel::onEvent,
            )
        }



        composable(
            route = Screen.PetitionScreen.route + "/{petitionId}",
            arguments = listOf(
                navArgument("petitionId") {
                    type = NavType.StringType
                    defaultValue = "123"
                    nullable = true
                },
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "myapp://green_signal/${Screen.PetitionScreen.route}/{petitionId}" })) { entry ->
            val petitionViewModel = hiltViewModel<PetitionViewModel>()

            PetitionScreen(
                petitionId = entry.arguments?.getString("petitionId")!!,
                navController = navController,
                state = petitionViewModel.state,
                onEvent = petitionViewModel::onEvent,
            )
        }

        composable(route = Screen.PetitionListScreen.route) {
            val petitionListViewModel = hiltViewModel<PetitionListViewModel>()

            PetitionListScreen(
                navController = navController,
                state = petitionListViewModel.state,
                onEvent = petitionListViewModel::onEvent
            )
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return  viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}
