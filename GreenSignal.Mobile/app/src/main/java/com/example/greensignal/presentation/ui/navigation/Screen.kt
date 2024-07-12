package com.example.greensignal.presentation.ui.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object AuthenticationScreen : Screen("authentication_screen")
    object UpdateInspectorScreen : Screen("update_inspector_screen")
    object InspectorProfileScreen : Screen("account_screen")
    object PersonalAccountScreen : Screen("personal_account_screen")
    object CreateIncidentScreen : Screen("create_incident_screen")
    object IncidentListScreen : Screen("incident_list_screen")
    object IncidentScreen : Screen("incident_screen")
    object MessageListScreen : Screen("message_list_screen")
    object PetitionListScreen : Screen("petitions_list_screen")
    object RatingScreen : Screen("rating_screen")
    object MessageScreen : Screen("message_screen")
    object CreateIncidentReportScreen : Screen("create_incident_report_screen")
    object  IncidentReportListScreen : Screen("incident_report_list_screen")
    object IncidentReportScreen : Screen("incident_report_screen")
    object UpdateIncidentReportScreen : Screen("update_incident_report_screen")
    object SessionListScreen : Screen("session_list_screen")
    object CreatePetitionScreen : Screen("create_petition_screen")
    object PetitionScreen : Screen("petition_screen")

    fun withArgs(vararg args: String?): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
