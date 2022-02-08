package com.example.examer.ui.navigation

sealed class ExamerDestinations(val route: String) {
    object HomeScreen :
        ExamerDestinations("ExamerDestinations.HomeScreen")

    object LoggedInScreen : ExamerDestinations("ExamerDestinations.LoggedInRoute")
    object TestHistoryScreen : ExamerDestinations("ExamerDestinations.TestHistoryScreen")
}