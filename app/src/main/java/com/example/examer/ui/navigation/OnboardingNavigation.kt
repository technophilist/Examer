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


@ExperimentalComposeUiApi
@ExperimentalPagerApi
fun NavGraphBuilder.onboardingGraph(
    route: String,
    navController: NavHostController,
    signupViewModelFactory: SignUpViewModelFactory,
    logInViewModelFactory: LogInViewModelFactory,
    onSuccessfulAuthentication: () -> Unit,
) {
//    navigation(startDestination = OnBoardingDestinations.kt.WelcomeScreen.route, route = route) {
//
//        composable(OnBoardingDestinations.kt.WelcomeScreen.route) {
//            WelcomeScreen(
//                onCreateAccountButtonClick = { navController.navigate(OnBoardingDestinations.kt.SignUpScreen.route) },
//                onLoginButtonClick = { navController.navigate(OnBoardingDestinations.kt.LoginScreen.route) }
//            )
//        }
//
//        composable(OnBoardingDestinations.kt.LoginScreen.route) {
//            LoginScreen(
//                viewModel = viewModel(
//                    factory = logInViewModelFactory,
//                    viewModelStoreOwner = it
//                ),
//                onSuccessfulAuthentication = onSuccessfulAuthentication
//            )
//        }
//
//        composable(OnBoardingDestinations.kt.SignUpScreen.route) {
//            SignUpScreen(
//                viewModel = viewModel(
//                    factory = signupViewModelFactory,
//                    viewModelStoreOwner = it
//                ),
//                onAccountCreatedSuccessfully = onSuccessfulAuthentication
//            )
//        }
//    }
}
