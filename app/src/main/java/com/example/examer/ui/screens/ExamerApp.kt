package com.example.examer.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.examer.auth.FirebaseAuthenticationService
import com.example.examer.di.AppContainer
import com.example.examer.ui.navigation.ExamerDestinations
import com.example.examer.ui.navigation.onboardingGraph
import com.example.examer.viewmodels.ExamerLogInViewModel
import com.example.examer.viewmodels.ExamerSignUpViewModel

import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Composable
fun ExamerApp(appContainer: AppContainer) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ExamerDestinations.Onboarding.route
    ) {
        onboardingGraph(
            route = ExamerDestinations.Onboarding.route,
            navController = navController,
            signupViewModelFactory = appContainer.signUpViewModelFactory,
            logInViewModelFactory = appContainer.logInViewModelFactory,
            onSuccessfulAuthentication = { TODO() }
        )
    }
}