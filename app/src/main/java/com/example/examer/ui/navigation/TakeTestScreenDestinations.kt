package com.example.examer.ui.navigation

sealed class TakeTestScreenDestinations(val route: String) {
    object ListenToAudioScreen : TakeTestScreenDestinations("com.example.examer.ui.navigation")
}