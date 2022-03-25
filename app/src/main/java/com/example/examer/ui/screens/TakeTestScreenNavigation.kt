package com.example.examer.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.examer.data.domain.MultiChoiceQuestion
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
import timber.log.Timber

fun NavGraphBuilder.takeTestScreenNavigation(
    navController: NavHostController,
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
                val numberOfRepeatsLeftForAudioFile =
                    testSessionViewModel.numberOfRepeatsLeftForAudioFile
                val isAudioFilePlaying by testSessionViewModel.isAudioFilePlaying
                val isAudioIconClickEnabled by derivedStateOf {
                    numberOfRepeatsLeftForAudioFile.value > 0 && !isAudioFilePlaying
                }
                val audioPlaybackState = remember {
                    AudioPlaybackState(
                        isEnabled = isAudioPlaybackEnabled,
                        progress = testSessionViewModel.playbackProgress,
                        numberOfRepeatsLeft = numberOfRepeatsLeftForAudioFile
                    )
                }
                ListenToAudioScreen(
                    timerState = timerState,
                    workBookState = workBookState,
                    audioPlayBackState = audioPlaybackState,
                    onNavigateToWorkBook = {
                        val currentWorkBookIndex = workBookState.currentWorkBookNumber.value - 1
                        val workBook = workBookList[currentWorkBookIndex]
                        val routeString =
                            TakeTestScreenDestinations.WorkBookScreen.buildRoute(workBook.questions)
                        navController.navigate(routeString)
                    },
                    isAudioIconClickEnabled = isAudioIconClickEnabled,
                    onAudioIconClick = testSessionViewModel::playAudioForCurrentWorkBook
                )
            }
        }
        composable(route = TakeTestScreenDestinations.WorkBookScreen.route) {
            it.arguments?.let { bundle ->
                val multiChoiceQuestionList = Json.decodeFromString<List<MultiChoiceQuestion>>(
                    bundle.getString(TakeTestScreenDestinations.WorkBookScreen.QUESTIONS_LIST_ARG)!!
                )
                WorkBookScreen(questionList = multiChoiceQuestionList)
            }
        }
    }
}