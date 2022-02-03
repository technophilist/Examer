package com.example.examer.di

import com.example.examer.auth.FirebaseAuthenticationService
import com.example.examer.data.ExamerRepository
import com.example.examer.data.Repository
import com.example.examer.data.remote.FirebaseRemoteDatabase
import com.example.examer.data.remote.RemoteDatabase
import com.example.examer.utils.HomeViewModelFactory
import com.example.examer.utils.LogInViewModelFactory
import com.example.examer.utils.SignUpViewModelFactory

class AppContainer {
    private val remoteDatabase =
        FirebaseRemoteDatabase(StandardDispatchersProvider()) as RemoteDatabase
    private val repository = ExamerRepository(remoteDatabase) as Repository
    
    val authenticationService = FirebaseAuthenticationService()
    val isUserLoggedIn get() = authenticationService.currentUser != null
    val logInViewModelFactory = LogInViewModelFactory(authenticationService)
    val signUpViewModelFactory = SignUpViewModelFactory(authenticationService)
    val homeViewModelFactory = HomeViewModelFactory(authenticationService, repository)


}