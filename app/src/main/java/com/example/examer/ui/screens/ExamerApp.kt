package com.example.examer.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.examer.data.domain.ExamerUser
import com.example.examer.di.AppContainer
import com.example.examer.ui.components.ExamerNavigationScaffold
import com.example.examer.ui.navigation.ExamerDestinations
import com.example.examer.ui.navigation.OnBoardingDestinations
import com.example.examer.ui.screens.onboarding.LoginScreen
import com.example.examer.ui.screens.onboarding.SignUpScreen
import com.example.examer.ui.screens.onboarding.WelcomeScreen
import com.example.examer.viewmodels.ExamerHomeViewModel

import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Composable
fun ExamerApp(appContainer: AppContainer) {
    val onBoardingNavController = rememberNavController()
    val loggedInNavController = rememberNavController()
    val onSuccessfulAuthentication = remember {
        { onBoardingNavController.navigate(ExamerDestinations.LoggedInScreen.route) }
    }
    NavHost(
        navController = onBoardingNavController,
        startDestination = if (appContainer.isUserLoggedIn) ExamerDestinations.LoggedInScreen.route
        else OnBoardingDestinations.WelcomeScreen.route
    ) {
        composable(OnBoardingDestinations.WelcomeScreen.route) {
            WelcomeScreen(
                onCreateAccountButtonClick = {
                    onBoardingNavController.navigate(
                        OnBoardingDestinations.SignUpScreen.route
                    )
                },
                onLoginButtonClick = { onBoardingNavController.navigate(OnBoardingDestinations.LoginScreen.route) }
            )
        }

        composable(OnBoardingDestinations.LoginScreen.route) {
            LoginScreen(
                viewModel = viewModel(
                    factory = appContainer.logInViewModelFactory,
                    viewModelStoreOwner = it
                ),
                onSuccessfulAuthentication = onSuccessfulAuthentication
            )
        }

        composable(OnBoardingDestinations.SignUpScreen.route) {
            SignUpScreen(
                viewModel = viewModel(
                    factory = appContainer.signUpViewModelFactory,
                    viewModelStoreOwner = it
                ),
                onAccountCreatedSuccessfully = onSuccessfulAuthentication
            )
        }

        composable(ExamerDestinations.LoggedInScreen.route) {
            LoggedInScreen(navHostController = loggedInNavController, appContainer = appContainer)
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun LoggedInScreen(navHostController: NavHostController, appContainer: AppContainer) {
    ExamerNavigationScaffold(
        currentlyLoggedInUser = appContainer.authenticationService.currentUser!!,
        navigationDrawerDestinations = emptyList()
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navHostController,
            startDestination = ExamerDestinations.HomeScreen.route
        ) {
            composable(route = ExamerDestinations.HomeScreen.route) {
                val homeViewModel = viewModel<ExamerHomeViewModel>(
                    factory = appContainer.homeViewModelFactory,
                    viewModelStoreOwner = it
                )
                val testList by homeViewModel.testDetailsList
                HomeScreen(tests = testList)
            }
        }
    }
}