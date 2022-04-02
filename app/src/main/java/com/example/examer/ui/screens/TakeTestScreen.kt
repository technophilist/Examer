package com.example.examer.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.primarySurface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.examer.R
import com.example.examer.data.domain.MultiChoiceQuestion
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.UserAnswers
import com.example.examer.data.domain.WorkBook
import com.example.examer.di.AppContainer
import com.example.examer.ui.navigation.TakeTestScreenDestinations
import com.example.examer.ui.screens.listenToAudioScreen.AudioPlaybackState
import com.example.examer.ui.screens.listenToAudioScreen.ListenToAudioScreen
import com.example.examer.ui.screens.listenToAudioScreen.TimerState
import com.example.examer.ui.screens.listenToAudioScreen.WorkBookState
import com.example.examer.utils.WorkBookViewModelFactory
import com.example.examer.viewmodels.ExamerTestSessionViewModel
import com.example.examer.viewmodels.ExamerWorkBookViewModel
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Composable
fun TakeTestScreen(
    appContainer: AppContainer,
    viewModelStoreOwner: ViewModelStoreOwner,
    testDetails: TestDetails,
    workBookList: List<WorkBook>
) {
    val navController = rememberNavController()
    val testSessionViewModel = viewModel<ExamerTestSessionViewModel>(
        factory = appContainer.getTestSessionViewModelFactory(
            testDetails,
            workBookList
        ),
        viewModelStoreOwner = viewModelStoreOwner
    )
    val workBookState = remember {
        WorkBookState(
            testSessionViewModel.currentWorkBookNumber,
            10 // TODO hardcoded
        )
    }
    val timerState = remember {
        TimerState(
            hoursRemaining = testSessionViewModel.hoursRemaining,
            minutesRemaining = testSessionViewModel.minutesRemaining,
            secondsRemaining = testSessionViewModel.secondsRemaining
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            modifier = Modifier
                .background(MaterialTheme.colors.primarySurface)
                .statusBarsPadding()
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        R.string.label_workbook,
                        workBookState.currentWorkBookNumber.value,
                        workBookState.totalNumberOfWorkBooks
                    )
                )
                Text(text = "${timerState.hoursRemaining.value} : ${timerState.minutesRemaining.value} : ${timerState.secondsRemaining.value}")
            }
        }
        NavHost(
            navController = navController,
            startDestination = TakeTestScreenDestinations.ListenToAudioScreen.route
        ) {
            composable(route = TakeTestScreenDestinations.ListenToAudioScreen.route) {
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
                        // the viewModel instance will not be destroyed until
                        // it is popped off the back stack. Keep the viewModel
                        // ready with the next workbook and navigate to the
                        // workbook screen. When the user is navigating from
                        // the workbook screen, pop the backstack instead of
                        // navigating back to this screen to play the audio file
                        // associated with the next question.
                        navController.navigate(routeString)
                        testSessionViewModel.moveToNextWorkBook()
                    },
                    isAudioIconClickEnabled = isAudioIconClickEnabled,
                    onAudioIconClick = testSessionViewModel::playAudioForCurrentWorkBook
                )
            }
            workBookScreenComposable(navController, appContainer.workBookViewModelFactory)
        }
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
            val workBookId =
                bundle.getString(TakeTestScreenDestinations.WorkBookScreen.WORKBOOK_ID_ARG)!!
            val testDetailsId =
                bundle.getString(TakeTestScreenDestinations.WorkBookScreen.TEST_DETAILS_ID_ARG)!!
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