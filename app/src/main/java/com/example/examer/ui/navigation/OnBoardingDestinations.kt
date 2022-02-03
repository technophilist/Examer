package com.example.examer.ui.navigation

sealed class OnBoardingDestinations(val route: String) {
    object WelcomeScreen : OnBoardingDestinations("com.example.examer.ui.navigation.WelcomeScreen")
    object SignUpScreen : OnBoardingDestinations("com.example.examer.ui.navigation.SignUpScreen")
    object LoginScreen : OnBoardingDestinations("com.example.examer.ui.navigation.LoginScreen")
}
