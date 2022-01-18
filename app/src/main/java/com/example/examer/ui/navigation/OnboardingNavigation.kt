package com.example.examer.ui.navigation

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.examer.ui.screens.onboarding.LoginScreen
import com.example.examer.ui.screens.onboarding.SignUpScreen
import com.example.examer.ui.screens.onboarding.WelcomeScreen
import com.example.examer.utils.LogInViewModelFactory
import com.example.examer.utils.SignUpViewModelFactory
import com.example.examer.viewmodels.LogInViewModel
import com.example.examer.viewmodels.SignUpViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

sealed class OnBoardingDestinations(val route: String) {
    object WelcomeScreen : OnBoardingDestinations("com.example.examer.ui.navigation.WelcomeScreen")
    object SignUpScreen : OnBoardingDestinations("com.example.examer.ui.navigation.SignUpScreen")
    object LoginScreen : OnBoardingDestinations("com.example.examer.ui.navigation.LoginScreen")
}

@ExperimentalComposeUiApi
@ExperimentalPagerApi
fun NavGraphBuilder.onboardingGraph(
    route: String,
    navController: NavHostController,
    signupViewModelFactory: SignUpViewModelFactory,
    logInViewModelFactory: LogInViewModelFactory,
    onSuccessfulAuthentication: () -> Unit,
) {
    navigation(startDestination = OnBoardingDestinations.WelcomeScreen.route, route = route) {

        composable(OnBoardingDestinations.WelcomeScreen.route) {
            WelcomeScreen(
                onCreateAccountButtonClick = { navController.navigate(OnBoardingDestinations.SignUpScreen.route) },
                onLoginButtonClick = { navController.navigate(OnBoardingDestinations.LoginScreen.route) }
            )
        }

        composable(OnBoardingDestinations.LoginScreen.route) {
            LoginScreen(
                viewModel = viewModel(
                    factory = logInViewModelFactory,
                    viewModelStoreOwner = it
                ),
                onSuccessfulAuthentication = onSuccessfulAuthentication
            )
        }

        composable(OnBoardingDestinations.SignUpScreen.route) {
            SignUpScreen(
                viewModel = viewModel(
                    factory = signupViewModelFactory,
                    viewModelStoreOwner = it
                ),
                onAccountCreatedSuccessfully = onSuccessfulAuthentication
            )
        }
    }
}
