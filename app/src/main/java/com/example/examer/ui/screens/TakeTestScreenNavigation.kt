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

private const val TEST_DETAILS_ARGUMENT =
    "com.example.examer.ui.screens.TEST_DETAILS_ARGUMENT"
private const val WORKBOOK_LIST_ARGUMENT =
    "com.example.examer.ui.screens.WORKBOOK_LIST_ARGUMENT"

fun NavGraphBuilder.takeTestScreenNavigation(
    appContainer: AppContainer,
    route: String
) {
    navigation(
        route = route,
        startDestination = TakeTestScreenDestinations.ListenToAudioScreen.route,
    ) {
        composable(
            route = TakeTestScreenDestinations.ListenToAudioScreen.route,
            arguments = listOf(
                navArgument(TEST_DETAILS_ARGUMENT) {
                    type = NavType.SerializableType(TestDetails::class.java)
                    nullable = false
                },
                navArgument(WORKBOOK_LIST_ARGUMENT) {
                    type = NavType.SerializableArrayType(WorkBook::class.java)
                    nullable = false
                }
            )
        ) {
            it.arguments?.let { bundle ->
                val testSessionViewModel = viewModel<ExamerTestSessionViewModel>(
                    factory = appContainer.getTestSessionViewModelFactory(
                        testDetails = bundle[TEST_DETAILS_ARGUMENT] as TestDetails,
                        workBookList = (bundle[WORKBOOK_LIST_ARGUMENT] as Array<WorkBook>).toList()
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
                    timeInfo = timerState,
                    workBookState = workBookState,
                    audioPlayBackState = audioPlaybackState,
                    onNavigateToWorkBook = { /*TODO*/ }
                )
            }
        }
    }
}