package com.example.examer.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.CachePolicy
import com.example.examer.R
import com.example.examer.data.domain.ExamerUser
import com.example.examer.di.AppContainer
import com.example.examer.ui.components.ExamerNavigationScaffold
import com.example.examer.ui.components.NavigationDrawerDestination
import com.example.examer.ui.navigation.ExamerDestinations
import com.example.examer.ui.navigation.OnBoardingDestinations
import com.example.examer.ui.screens.onboarding.LoginScreen
import com.example.examer.ui.screens.onboarding.SignUpScreen
import com.example.examer.ui.screens.onboarding.WelcomeScreen
import com.example.examer.viewmodels.profileScreenViewModel.updateEmail
import com.example.examer.viewmodels.profileScreenViewModel.updateName
import com.example.examer.viewmodels.profileScreenViewModel.updatePassword
import com.example.examer.viewmodels.profileScreenViewModel.ExamerProfileScreenViewModel
import com.example.examer.viewmodels.ExamerTestsViewModel
import com.example.examer.viewmodels.profileScreenViewModel.ProfileScreenViewModel
import com.example.examer.viewmodels.TestsViewModelUiState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Composable
fun ExamerApp(appContainer: AppContainer) {
    val onBoardingNavController = rememberNavController()
    val onSuccessfulAuthentication = remember {
        {
            onBoardingNavController.navigate(ExamerDestinations.LoggedInScreen.route) {
                popUpTo(OnBoardingDestinations.WelcomeScreen.route) { inclusive = true }
            }
        }
    }
    val currentlyLoggedInUser by appContainer.authenticationService.currentUser.observeAsState()
    NavHost(
        navController = onBoardingNavController,
        startDestination = if (currentlyLoggedInUser != null)
            ExamerDestinations.LoggedInScreen.route else OnBoardingDestinations.WelcomeScreen.route
    ) {
        composable(OnBoardingDestinations.WelcomeScreen.route) {
            WelcomeScreen(
                onCreateAccountButtonClick = {
                    onBoardingNavController.navigate(OnBoardingDestinations.SignUpScreen.route)
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
            LoggedInScreen(
                onSignOut = {
                    onBoardingNavController.navigate(OnBoardingDestinations.WelcomeScreen.route) {
                        popUpTo(ExamerDestinations.LoggedInScreen.route) { inclusive = true }
                        appContainer.authenticationService.signOut()
                    }
                },
                appContainer = appContainer,
                currentlyLoggedInUser = currentlyLoggedInUser!!
            )
        }
    }
}


