package com.example.examer.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.examer.R
import com.example.examer.data.domain.MultiChoiceQuestion
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.UserAnswers
import com.example.examer.data.domain.WorkBook
import com.example.examer.di.AppContainer
import com.example.examer.ui.navigation.ExamerDestinations
import com.example.examer.ui.navigation.TakeTestScreenDestinations
import com.example.examer.ui.navigation.TakeTestScreenDestinations.WorkBookScreen.TEST_DETAILS_ID_ARG
import com.example.examer.ui.navigation.TakeTestScreenDestinations.WorkBookScreen.WORKBOOK_ID_ARG
import com.example.examer.ui.screens.listenToAudioScreen.AudioPlaybackState
import com.example.examer.ui.screens.listenToAudioScreen.ListenToAudioScreen
import com.example.examer.ui.screens.listenToAudioScreen.TimerState
import com.example.examer.ui.screens.listenToAudioScreen.WorkBookState
import com.example.examer.utils.WorkBookViewModelFactory
import com.example.examer.viewmodels.ExamerTestSessionViewModel
import com.example.examer.viewmodels.ExamerWorkBookViewModel
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
                            TakeTestScreenDestinations.WorkBookScreen.buildRoute(
                                testDetails.id,
                                workBook.id,
                                workBook.questions
                            )
                        navController.navigate(routeString)
                        // the viewModel instance will not be destroyed until
                        // it is popped off the back stack. Keep the viewModel
                        // ready with the next workbook and navigate to the
                        // workbook screen. When the user is navigating from
                        // the workbook screen, pop the backstack instead of
                        // navigating back to this screen to play the audio file
                        // associated with the next question.
                        testSessionViewModel.moveToNextWorkBook()
                    },
                    isAudioIconClickEnabled = isAudioIconClickEnabled,
                    onAudioIconClick = testSessionViewModel::playAudioForCurrentWorkBook
                )
            }
        }
        workBookScreenComposable(navController, appContainer.workBookViewModelFactory)
    }
}

private fun NavGraphBuilder.workBookScreenComposable(
    navController: NavHostController,
    workBookViewModelFactory: WorkBookViewModelFactory
) {
    composable(route = TakeTestScreenDestinations.WorkBookScreen.route) {
        BackHandler {
            /* TODO: Temporarily use this composable to not allow the user to navigate back.*/
        }
        it.arguments?.let { bundle ->
            val multiChoiceQuestionList = Json.decodeFromString<List<MultiChoiceQuestion>>(
                bundle.getString(TakeTestScreenDestinations.WorkBookScreen.QUESTIONS_LIST_ARG)!!
            )
            val workBookId = bundle.getString(WORKBOOK_ID_ARG)!!
            val testDetailsId = bundle.getString(TEST_DETAILS_ID_ARG)!!
            val viewModel = viewModel<ExamerWorkBookViewModel>(
                factory = workBookViewModelFactory,
                viewModelStoreOwner = it
            )
            WorkBookScreen(
                questionList = multiChoiceQuestionList,
                onFooterButtonClick = { answersMap ->
                    val userAnswers = UserAnswers(
                        associatedWorkBookId = workBookId,
                        answers = answersMap
                    )
                    viewModel.saveUserAnswersForTestId(userAnswers, testDetailsId)
                    // the viewModel instance of the ListenToAudioScreen
                    // composable will not be destroyed until it is popped
                    // off the back stack.The viewModel will be ready with
                    // the next workbook on navigating to this screen. Pop
                    // the backstack instead of navigating explicitly to
                    // ListenToAudioScreen to allow the user to play the
                    // audio file associated with the next question.
                    navController.popBackStack()
                }
            )
        }
    }
}