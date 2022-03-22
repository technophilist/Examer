package com.example.examer.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.WorkBook
import com.example.examer.di.AppContainer
import com.example.examer.ui.navigation.ExamerDestinations
import com.example.examer.ui.navigation.TakeTestScreenDestinations
import com.example.examer.ui.screens.listenToAudioScreen.AudioPlaybackState
import com.example.examer.ui.screens.listenToAudioScreen.ListenToAudioScreen
import com.example.examer.ui.screens.listenToAudioScreen.TimerState
import com.example.examer.ui.screens.listenToAudioScreen.WorkBookState
import com.example.examer.viewmodels.ExamerTestSessionViewModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun NavGraphBuilder.takeTestScreenNavigation(
    appContainer: AppContainer,
    route: String
) {
    navigation(
        route = route,
        startDestination = TakeTestScreenDestinations.ListenToAudioScreen.route,
    ) {
        composable(route = TakeTestScreenDestinations.ListenToAudioScreen.route) {
            it.arguments?.let { bundle ->
                val testDetails =
                    Json.decodeFromString<TestDetails>(bundle.getString(ExamerDestinations.TakeTestScreen.TEST_DETAILS_ARG)!!)
                val workBookList =
                    Json.decodeFromString<List<WorkBook>>(bundle.getString(ExamerDestinations.TakeTestScreen.WORKBOOK_LIST_ARG)!!)
                val testSessionViewModel = viewModel<ExamerTestSessionViewModel>(
                    factory = appContainer.getTestSessionViewModelFactory(
                        testDetails = testDetails,
                        workBookList = workBookList
                    ),
                    viewModelStoreOwner = it
                )
                val timerState = remember {
                    TimerState(
                        hoursRemaining = testSessionViewModel.hoursRemaining,
                        minutesRemaining = testSessionViewModel.minutesRemaining,
                        secondsRemaining = testSessionViewModel.secondsRemaining,
                    )
                }
                val workBookState = remember {
                    WorkBookState(
                        currentWorkBookNumber = testSessionViewModel.currentWorkBookNumber,
                        totalNumberOfWorkBooks = 10 // TODO Hard coded
                    )
                }
                val isAudioPlaybackEnabled = remember { mutableStateOf(true) }
                val audioPlaybackState = remember {
                    AudioPlaybackState(
                        isEnabled = isAudioPlaybackEnabled,
                        progress = testSessionViewModel.playbackProgress,
                        numberOfRepeatsLeft = testSessionViewModel.numberOfRepeatsLeftForAudioFile
                    )
                }
                // TODO change timeInfo param name to timerState
                ListenToAudioScreen(
                    timerState = timerState,
                    workBookState = workBookState,
                    audioPlayBackState = audioPlaybackState,
                    onNavigateToWorkBook = { /*TODO*/ }
                )
            }
        }
    }
}