package com.example.examer.ui.navigation

sealed class ExamerDestinations(val route: String) {
    object Onboarding :
        ExamerDestinations("ExamerDestinations.onBoarding")
    object HomeScreen:
            ExamerDestinations("ExamerDestinations.HomeScreen")
}