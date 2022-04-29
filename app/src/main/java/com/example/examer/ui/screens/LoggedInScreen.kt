package com.example.examer.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.CachePolicy
import com.example.examer.R
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.WorkBook
import com.example.examer.di.AppContainer
import com.example.examer.ui.components.CircularLoadingProgressOverlay
import com.example.examer.ui.components.ExamerNavigationScaffold
import com.example.examer.ui.components.NavigationDrawerDestination
import com.example.examer.ui.navigation.ExamerDestinations
import com.example.examer.viewmodels.*
import com.example.examer.viewmodels.profileScreenViewModel.*
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

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
    val isTopAppBarVisible by derivedStateOf {
        currentBackStackEntry?.destination?.route != ExamerDestinations.TakeTestScreen.route
    }
    var navigationIconImageVector by remember { mutableStateOf(Icons.Filled.Menu) }
    val isNavigationDrawerIconVisible by derivedStateOf {
        navigationIconImageVector == Icons.Filled.Menu && isTopAppBarVisible
    }
    val currentOnBackPressedDispatcher =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    // if the drawer is open, close the drawer instead of
    // quitting the app.
    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        coroutineScope.launch { scaffoldState.drawerState.close() }
    }
    ExamerNavigationScaffold(
        currentlyLoggedInUser = currentlyLoggedInUser,
        imagePainter = imagePainter,
        scaffoldState = scaffoldState,
        isTopAppBarVisible = isTopAppBarVisible,
        navigationIconImageVector = navigationIconImageVector,
        isDrawerGesturesEnabled = isNavigationDrawerIconVisible,
        onNavigationIconClick = {
            // if the up button is visible, then execute the callback that
            // the system back button would use. This will help
            // to pop the back stack of the nested nav graphs.
            if (isNavigationDrawerIconVisible) coroutineScope.launch { scaffoldState.drawerState.open() }
            else currentOnBackPressedDispatcher?.onBackPressed()
        },
        isNavigationDrawerDestinationSelected = {
            // highlight the navigation destination if and only if,
            // the current destination's route exists as a key in
            // in the map and the associated value is equal to
            // the NavigationDrawerDestination's name.
            navigationDrawerDestinationRouteAndNameMap[currentBackStackEntry?.destination?.route] == it.name
        },
        navigationDrawerDestinations = navigationDrawerDestinations,
        onSignOutButtonClick = { isAlertDialogVisible = true },
        content = { paddingValues ->
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
                scheduledTestsComposable(
                    route = ExamerDestinations.ScheduledTestsScreen.route,
                    appContainer = appContainer,
                    loggedInNavController = loggedInNavController,
                    snackbarHostState = scaffoldState.snackbarHostState,
                    coroutineScope = coroutineScope
                )

                testHistoryScreenComposable(
                    route = ExamerDestinations.TestHistoryScreen.route,
                    appContainer = appContainer
                )

                profileScreenComposable(
                    route = ExamerDestinations.ProfileScreen.route,
                    appContainer = appContainer,
                    currentlyLoggedInUser = currentlyLoggedInUser,
                    onNavigateToEditScreen = { navigationIconImageVector = Icons.Filled.ArrowBack },
                    onNavigateFromEditScreen = { navigationIconImageVector = Icons.Filled.Menu },
                    snackbarHostState = scaffoldState.snackbarHostState
                )

                takeTestScreenComposable(
                    route = ExamerDestinations.TakeTestScreen.route,
                    appContainer = appContainer,
                    navController = loggedInNavController,
                )
            }
        },
    )
}

private fun NavGraphBuilder.takeTestScreenComposable(
    route: String,
    appContainer: AppContainer,
    navController: NavController
) {
    composable(route = route) { backStackEntry ->
        val navArguments = backStackEntry.arguments!!
        val testDetails = navArguments
            .getString(ExamerDestinations.TakeTestScreen.TEST_DETAILS_ARG)!!
            .let { Json.decodeFromString<TestDetails>(it) }
        val workBookList = navArguments
            .getString(ExamerDestinations.TakeTestScreen.WORKBOOK_LIST_ARG)!!
            .let { Json.decodeFromString<List<WorkBook>>(it) }
        val testSessionViewModel = viewModel<ExamerTestSessionViewModel>(
            factory = appContainer.getTestSessionViewModelFactory(testDetails, workBookList),
            viewModelStoreOwner = backStackEntry
        )
        val testSessionUiState by testSessionViewModel.uiState
        val isTestSessionTimedOut = remember(testSessionUiState) {
            testSessionUiState == TestSessionViewModel.UiState.TEST_TIMED_OUT
        }
        var isBackButtonPressed by remember { mutableStateOf(false) }
        // alert dialog must be visible when the user clicks on finish test button.
        var isFinishTestAlertDialogVisible by remember { mutableStateOf(false) }
        // display an alert dialog when the user is trying to quit the test by clicking
        // the back button while taking the test.
        var isQuitTestAlertDialogVisible by remember { mutableStateOf(false) }
        // display an alert dialog when the user is exiting the test using the exit test
        // icon in the app bar.
        var isExitAlertDialogVisible by remember { mutableStateOf(false) }
        val onAlertDialogConfirmButtonClick = {
            testSessionViewModel.stopAudioPlayback()
            testSessionViewModel.markCurrentTestAsComplete()
            navController.navigate(ExamerDestinations.ScheduledTestsScreen.route) {
                popUpTo(ExamerDestinations.TakeTestScreen.route) { inclusive = true }
            }
        }
        if (isQuitTestAlertDialogVisible) {
            TakeTestScreenComposableAlertDialogBoxes.QuitTestAlertDialog(
                onConfirmButtonClick = {
                    isQuitTestAlertDialogVisible = false
                    onAlertDialogConfirmButtonClick()
                },
                onDismissButtonClick = {
                    if (isBackButtonPressed) isBackButtonPressed = false
                    isQuitTestAlertDialogVisible = false
                },
                onDismissRequest = {
                    if (isBackButtonPressed) isBackButtonPressed = false
                    isQuitTestAlertDialogVisible = false
                }
            )
        }
        if (isExitAlertDialogVisible) {
            TakeTestScreenComposableAlertDialogBoxes.ExitAlertDialog(
                onConfirmButtonClick = {
                    isExitAlertDialogVisible = false
                    onAlertDialogConfirmButtonClick()
                },
                onDismissButtonClick = {
                    if (isBackButtonPressed) isBackButtonPressed = false
                    isExitAlertDialogVisible = false
                },
                onDismissRequest = {
                    if (isBackButtonPressed) isBackButtonPressed = false
                    isExitAlertDialogVisible = false
                }
            )
        }
        // if the test session timed out, show alert dialog box that
        // cannot be dismissed.
        if (isTestSessionTimedOut) {
            TakeTestScreenComposableAlertDialogBoxes.TestSessionTimedOutAlertDialog(
                onConfirmButtonClick = onAlertDialogConfirmButtonClick
            )
        }
        if (isFinishTestAlertDialogVisible) {
            TakeTestScreenComposableAlertDialogBoxes.FinishTestAlertDialog(
                onDismissRequest = { isFinishTestAlertDialogVisible = false },
                onConfirmButtonClick = {
                    isFinishTestAlertDialogVisible = false
                    onAlertDialogConfirmButtonClick()
                },
                onDismissButtonClick = { isFinishTestAlertDialogVisible = false }
            )
        }
        TakeTestScreen(
            appContainer = appContainer,
            testSessionViewModel = testSessionViewModel,
            onExitTestButtonClick = { isExitAlertDialogVisible = true },
            onFinishTestButtonClick = { isFinishTestAlertDialogVisible = true },
            testDetails = testDetails
        )
        BackHandler {
            isBackButtonPressed = true
            isQuitTestAlertDialogVisible = true
        }
    }
}

@ExperimentalCoilApi
private fun NavGraphBuilder.profileScreenComposable(
    route: String,
    appContainer: AppContainer,
    currentlyLoggedInUser: ExamerUser,
    onNavigateToEditScreen: () -> Unit,
    onNavigateFromEditScreen: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    composable(route = route) { navBackStackEntry ->
        val resources = LocalContext.current.resources
        val profileScreenViewModel = viewModel<ExamerProfileScreenViewModel>(
            factory = appContainer.profileScreenViewModelFactory,
            viewModelStoreOwner = navBackStackEntry
        )
        val profileScreenUiState by profileScreenViewModel.uiState
        DefaultExamerProfileScreen(
            currentlyLoggedInUser = currentlyLoggedInUser,
            onNavigateToEditScreen = onNavigateToEditScreen,
            onNavigateFromEditScreen = onNavigateFromEditScreen,
            isLoadingOverlayVisible = profileScreenUiState == ProfileScreenViewModel.UiState.LOADING,
            updateProfilePicture = profileScreenViewModel::updateProfilePicture,
            updateName = profileScreenViewModel::updateName,
            updateEmail = profileScreenViewModel::updateEmail,
            updatePassword = profileScreenViewModel::updatePassword,
            isValidEmail = profileScreenViewModel::isValidEmail,
            isValidPassword = profileScreenViewModel::isValidPassword
        )
        LaunchedEffect(profileScreenUiState) {
            snackbarHostState.currentSnackbarData?.dismiss()
            if (profileScreenUiState == ProfileScreenViewModel.UiState.UPDATE_SUCCESS) {
                snackbarHostState.showSnackbar(resources.getString(R.string.snackbar_updated_successfully))
            } else if (profileScreenUiState == ProfileScreenViewModel.UiState.UPDATE_FAILURE) {
                snackbarHostState.showSnackbar(resources.getString(R.string.snackbar_update_failure))
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
private fun NavGraphBuilder.testHistoryScreenComposable(
    route: String,
    appContainer: AppContainer
) {
    composable(route = route) {
        val previousTestsViewModelFactory = appContainer.previousTestsViewModelFactory
        val previousTestsViewModel = viewModel<ExamerPreviousTestsViewModel>(
            factory = previousTestsViewModelFactory,
            viewModelStoreOwner = it
        )
        val swipeRefreshState = rememberSwipeRefreshState(
            isRefreshing = previousTestsViewModel.uiState.value == PreviousTestsViewModelUiState.LOADING
        )
        TestHistoryScreen(
            swipeRefreshState = swipeRefreshState,
            onRefresh = previousTestsViewModel::refreshPreviousTestsList,
            testResultsMap = previousTestsViewModel.testResultsMap.value
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
private fun NavGraphBuilder.scheduledTestsComposable(
    route: String,
    appContainer: AppContainer,
    loggedInNavController: NavHostController,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    composable(route = route) {
        val resources = LocalContext.current.resources
        val scheduledTestsViewModelFactory = appContainer.scheduledTestsViewModelFactory
        val testsViewModel = viewModel<ExamerTestsViewModel>(
            factory = scheduledTestsViewModelFactory,
            viewModelStoreOwner = it
        )
        val swipeRefreshState = rememberSwipeRefreshState(
            isRefreshing = testsViewModel.testsViewModelUiState.value == TestsViewModelUiState.LOADING
        )
        val testList by testsViewModel.testDetailsList
        var isTestLoading by remember { mutableStateOf(false) }
        val onTakeTestButtonClick = { selectedTestDetails: TestDetails ->
            isTestLoading = true
            testsViewModel.fetchWorkBookListForTestDetails(
                selectedTestDetails,
                onSuccess = { workBookList ->
                    val takeTestScreenRoute = ExamerDestinations.TakeTestScreen.buildRoute(
                        testDetails = selectedTestDetails,
                        workBookList = workBookList
                    )
                    loggedInNavController.navigate(takeTestScreenRoute) {
                        popUpTo(ExamerDestinations.ScheduledTestsScreen.route) { inclusive = true }
                    }
                },
                onFailure = {
                    isTestLoading = false
                    coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(resources.getString(R.string.label_network_error_message))
                    }
                }
            )

        }
        CircularLoadingProgressOverlay(isOverlayVisible = isTestLoading) {
            ScheduledTestsScreen(
                tests = testList,
                swipeRefreshState = swipeRefreshState,
                onRefresh = testsViewModel::refreshTestDetailsList,
                onStartTest = onTakeTestButtonClick,
                onTestExpired = { testDetailsItem ->
                    testsViewModel.markTestAsMissed(testDetailsItem)
                    testsViewModel.refreshTestDetailsList()
                }
            )
        }
    }
}

