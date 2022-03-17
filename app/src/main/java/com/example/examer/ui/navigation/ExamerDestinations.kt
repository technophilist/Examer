package com.example.examer.ui.navigation

sealed class ExamerDestinations(val route: String) {
    object ScheduledTestsScreen :
        ExamerDestinations("ExamerDestinations.ScheduledTestsScreen")

    object LoggedInScreen : ExamerDestinations("ExamerDestinations.LoggedInRoute")
    object TestHistoryScreen : ExamerDestinations("ExamerDestinations.TestHistoryScreen")
    object ProfileScreen : ExamerDestinations("ExamerDestinations.ProfileScreen")
    object TakeTestScreen : ExamerDestinations("ExamerDestinations.ListenToAudioScreen")
}