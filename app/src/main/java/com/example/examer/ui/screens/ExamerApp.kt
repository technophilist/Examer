package com.example.examer.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
import com.example.examer.viewmodels.ExamerHomeViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

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
    NavHost(
        navController = onBoardingNavController,
        startDestination = if (appContainer.isUserLoggedIn) ExamerDestinations.HomeScreen.route
        else OnBoardingDestinations.WelcomeScreen.route
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
            appContainer.authenticationService.currentUser?.let { currentUser ->
                LoggedInScreen(
                    onSignOut = {
                        onBoardingNavController.navigate(OnBoardingDestinations.WelcomeScreen.route) {
                            popUpTo(ExamerDestinations.LoggedInScreen.route) { inclusive = true }
                            appContainer.authenticationService.signOut()
                        }
                    },
                    appContainer = appContainer,
                    currentlyLoggedInUser = currentUser
                )
            }

        }
    }
}


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun LoggedInScreen(
    onSignOut: () -> Unit,
    appContainer: AppContainer,
    currentlyLoggedInUser: ExamerUser,
) {
    val loggedInNavController = rememberNavController()
    var isAlertDialogVisible by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val resources = LocalContext.current.resources
    val currentBackStackEntry by loggedInNavController.currentBackStackEntryAsState()
    // a map the associates the route string of a screen in ExamerDestinations,
    // to a string representing that route in the UI.
    val navigationDrawerDestinationRouteAndNameMap = remember {
        mapOf(
            ExamerDestinations.HomeScreen.route to resources.getString(R.string.navigation_drawer_label_scheduled_test),
            ExamerDestinations.TestHistoryScreen.route to resources.getString(R.string.navigation_drawer_label_test_history)
        )
    }
    val navigationDrawerDestinations = remember {
        listOf(
            NavigationDrawerDestination(
                icon = Icons.Filled.List,
                name = navigationDrawerDestinationRouteAndNameMap.getValue(ExamerDestinations.HomeScreen.route),
                onClick = {
                    if (currentBackStackEntry?.destination?.route != ExamerDestinations.HomeScreen.route) {
                        loggedInNavController.navigate(ExamerDestinations.HomeScreen.route)
                    }
                }
            ),
            NavigationDrawerDestination(
                icon = Icons.Filled.History,
                name = navigationDrawerDestinationRouteAndNameMap.getValue(ExamerDestinations.TestHistoryScreen.route),
                onClick = {
                    if (currentBackStackEntry?.destination?.route != ExamerDestinations.TestHistoryScreen.route) {
                        loggedInNavController.navigate(ExamerDestinations.TestHistoryScreen.route)
                    }
                }
            )
        )
    }
    ExamerNavigationScaffold(
        scaffoldState = scaffoldState,
        currentlyLoggedInUser = currentlyLoggedInUser,
        navigationDrawerDestinations = navigationDrawerDestinations,
        onSignOutButtonClick = { isAlertDialogVisible = true },
        isNavigationDrawerDestinationSelected = {
            // highlight the navigation destination if and only if,
            // the current destination's route exists as a key in
            // in the map and the associated value is equal to
            // the NavigationDrawerDestination's name.
            navigationDrawerDestinationRouteAndNameMap[currentBackStackEntry?.destination?.route] == it.name
        }
    ) { paddingValues ->
        if (isAlertDialogVisible) {
            LaunchedEffect(Unit) {
                scaffoldState.drawerState.animateTo(DrawerValue.Closed, tween())
            }
            AlertDialog(
                title = { Text(text = stringResource(R.string.alert_dialog_label_header)) },
                text = { Text(text = stringResource(R.string.alert_dialog_label_sign_out_description)) },
                confirmButton = {
                    TextButton(
                        onClick = onSignOut,
                        content = { Text(text = stringResource(R.string.alert_dialog_button_label_sign_out).uppercase()) }
                    )
                },
                dismissButton = {
                    TextButton(
                        onClick = { isAlertDialogVisible = false },
                        content = { Text(text = stringResource(R.string.alert_dialog_button_label_cancel).uppercase()) }
                    )
                },
                onDismissRequest = { isAlertDialogVisible = false }
            )
        }
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = loggedInNavController,
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
            composable(route = ExamerDestinations.TestHistoryScreen.route) {
                // TODO replace placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                )

            }
        }
    }
}
