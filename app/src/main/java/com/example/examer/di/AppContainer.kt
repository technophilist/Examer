package com.example.examer.di

import com.example.examer.auth.FirebaseAuthenticationService
import com.example.examer.utils.LogInViewModelFactory
import com.example.examer.utils.SignUpViewModelFactory

class AppContainer {
    private val authenticationService = FirebaseAuthenticationService()
    val logInViewModelFactory = LogInViewModelFactory(authenticationService)
    val signUpViewModelFactory = SignUpViewModelFactory(authenticationService)

}