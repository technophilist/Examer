package com.example.examer.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
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
import com.example.examer.viewmodels.ExamerTestsViewModel
import com.example.examer.viewmodels.TestsViewModelUiState
import com.example.examer.viewmodels.profileScreenViewModel.*
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun LoggedInScreen(
    onSignOut: () -> Unit,
    appContainer: AppContainer,
    currentlyLoggedInUser: ExamerUser,
) {
    val loggedInNavController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    var isAlertDialogVisible by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val resources = LocalContext.current.resources
    val currentBackStackEntry by loggedInNavController.currentBackStackEntryAsState()
    // a map the associates the route string of a screen in ExamerDestinations,
    // to a string representing that route in the UI.
    val navigationDrawerDestinationRouteAndNameMap = remember {
        mapOf(
            ExamerDestinations.ScheduledTestsScreen.route to resources.getString(R.string.navigation_drawer_label_scheduled_test),
            ExamerDestinations.TestHistoryScreen.route to resources.getString(R.string.navigation_drawer_label_test_history),
            ExamerDestinations.ProfileScreen.route to resources.getString(R.string.navigation_drawer_label_profile)
        )
    }
    val onNavigationDrawerDestinationClick = remember {
        { destinationRoute: String ->
            // pop the backstack if and only if the current route
            // is not ScheduledTestsScreen. This ensures that
            // the app always returns to the scheduled tests screen
            // when the back button is pressed from a different
            // destination.
            if (currentBackStackEntry?.destination?.route != ExamerDestinations.ScheduledTestsScreen.route) {
                loggedInNavController.popBackStack()
            }
            loggedInNavController.navigate(destinationRoute) { launchSingleTop = true }
            coroutineScope.launch { scaffoldState.drawerState.close() }
        }
    }
    val navigationDrawerDestinations = remember {
        listOf(
            NavigationDrawerDestination(
                icon = Icons.Filled.List,
                name = navigationDrawerDestinationRouteAndNameMap.getValue(ExamerDestinations.ScheduledTestsScreen.route),
                onClick = { onNavigationDrawerDestinationClick(ExamerDestinations.ScheduledTestsScreen.route) }
            ),
            NavigationDrawerDestination(
                icon = Icons.Filled.History,
                name = navigationDrawerDestinationRouteAndNameMap.getValue(ExamerDestinations.TestHistoryScreen.route),
                onClick = { onNavigationDrawerDestinationClick(ExamerDestinations.TestHistoryScreen.route) }
            ),
            NavigationDrawerDestination(
                icon = Icons.Filled.AccountCircle,
                name = navigationDrawerDestinationRouteAndNameMap.getValue(ExamerDestinations.ProfileScreen.route),
                onClick = { onNavigationDrawerDestinationClick(ExamerDestinations.ProfileScreen.route) }
            )
        )
    }
    // need to pass an empty string if photoUrl is null
    // else the error drawable will not be visible
    val imagePainter = rememberImagePainter(
        data = currentlyLoggedInUser.photoUrl ?: "",
        builder = {
            error(R.drawable.blank_profile_picture)
            diskCachePolicy(CachePolicy.ENABLED)
            crossfade(true)
        }
    )
    val navigationIconImageVector by remember {
        mutableStateOf(Icons.Filled.Menu)
    }

    // if the drawer is open, close the drawer instead of
    // quitting the app.
    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        coroutineScope.launch { scaffoldState.drawerState.close() }
    }
    ExamerNavigationScaffold(
        scaffoldState = scaffoldState,
        imagePainter = imagePainter,
        currentlyLoggedInUser = currentlyLoggedInUser,
        navigationIconImageVector = navigationIconImageVector,
        navigationDrawerDestinations = navigationDrawerDestinations,
        onSignOutButtonClick = { isAlertDialogVisible = true },
        isNavigationDrawerDestinationSelected = {
            // highlight the navigation destination if and only if,
            // the current destination's route exists as a key in
            // in the map and the associated value is equal to
            // the NavigationDrawerDestination's name.
            navigationDrawerDestinationRouteAndNameMap[currentBackStackEntry?.destination?.route] == it.name
        },
        onNavigationIconClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }
    ) { paddingValues ->
        if (isAlertDialogVisible) {
            LaunchedEffect(Unit) { scaffoldState.drawerState.close() }
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
            startDestination = ExamerDestinations.ScheduledTestsScreen.route
        ) {
            composable(route = ExamerDestinations.ScheduledTestsScreen.route) {
                val scheduledTestsViewModelFactory = appContainer.scheduledTestsViewModelFactory
                val testsViewModel = viewModel<ExamerTestsViewModel>(
                    factory = scheduledTestsViewModelFactory,
                    viewModelStoreOwner = it
                )
                val swipeRefreshState = rememberSwipeRefreshState(
                    isRefreshing = testsViewModel.testsViewModelUiState.value == TestsViewModelUiState.LOADING
                )
                ScheduledTestsScreen(
                    tests = testsViewModel.testDetailsList.value,
                    swipeRefreshState = swipeRefreshState,
                    onRefresh = testsViewModel::refreshTestDetailsList,
                    onTakeTestButtonClick = {}
                )
            }
            composable(route = ExamerDestinations.TestHistoryScreen.route) {
                val previousTestsViewModelFactory = appContainer.previousTestsViewModelFactory
                val testsViewModel = viewModel<ExamerTestsViewModel>(
                    factory = previousTestsViewModelFactory,
                    viewModelStoreOwner = it
                )
                val swipeRefreshState = rememberSwipeRefreshState(
                    isRefreshing = testsViewModel.testsViewModelUiState.value == TestsViewModelUiState.LOADING
                )
                TestHistoryScreen(
                    swipeRefreshState = swipeRefreshState,
                    onRefresh = testsViewModel::refreshTestDetailsList,
                    tests = testsViewModel.testDetailsList.value,
                    onReviewButtonClick = {}
                )
            }
            composable(route = ExamerDestinations.ProfileScreen.route) { navBackStackEntry ->
                val profileScreenViewModel = viewModel<ExamerProfileScreenViewModel>(
                    factory = appContainer.profileScreenViewModelFactory,
                    viewModelStoreOwner = navBackStackEntry
                )
                val profileScreenUiState by profileScreenViewModel.uiState
                DefaultExamerProfileScreen(
                    currentlyLoggedInUser = currentlyLoggedInUser,
                    isLoadingOverlayVisible = profileScreenUiState == ProfileScreenViewModel.UiState.LOADING,
                    updateProfilePicture = profileScreenViewModel::updateProfilePicture,
                    updateName = profileScreenViewModel::updateName,
                    updateEmail = profileScreenViewModel::updateEmail,
                    updatePassword = profileScreenViewModel::updatePassword,
                    isValidEmail = profileScreenViewModel::isValidEmail,
                    isValidPassword = profileScreenViewModel::isValidPassword
                )
                LaunchedEffect(profileScreenUiState) {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    if (profileScreenUiState == ProfileScreenViewModel.UiState.UPDATE_SUCCESS) {
                        scaffoldState.snackbarHostState.showSnackbar(resources.getString(R.string.snackbar_updated_successfully))
                    } else if (profileScreenUiState == ProfileScreenViewModel.UiState.UPDATE_FAILURE) {
                        scaffoldState.snackbarHostState.showSnackbar(resources.getString(R.string.snackbar_update_failure))
                    }
                }
            }
        }
    }
}
