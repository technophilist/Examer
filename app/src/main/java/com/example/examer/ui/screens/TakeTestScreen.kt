package com.example.examer.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
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
import com.example.examer.viewmodels.TestSessionViewModel
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Composable
fun TakeTestScreen(
    appContainer: AppContainer,
    testSessionViewModel:TestSessionViewModel,
    onExitTestButtonClick:()->Unit,
    testDetails: TestDetails,
    workBookList: List<WorkBook>
) {
    val navController = rememberNavController()
    val workBookState = remember {
        WorkBookState(
            testSessionViewModel.currentWorkBookNumber,
            testDetails.totalNumberOfQuestions //TODO Change variable name to totalNumberOfWorkbooks
        )
    }
    val timerState = remember {
        TimerState(
            hoursRemaining = testSessionViewModel.hoursRemaining,
            minutesRemaining = testSessionViewModel.minutesRemaining,
            secondsRemaining = testSessionViewModel.secondsRemaining
        )
    }
    val timerAndExitIconRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${timerState.hoursRemaining.value} : ${timerState.minutesRemaining.value} : ${timerState.secondsRemaining.value}")
            IconButton(
                onClick = onExitTestButtonClick,
                content = { Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = null) }
            )
        }
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.label_workbook,
                        workBookState.currentWorkBookNumber.value,
                        workBookState.totalNumberOfWorkBooks
                    )
                )
                timerAndExitIconRow()
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
                        navController.navigate(routeString)
                    },
                    isAudioIconClickEnabled = isAudioIconClickEnabled,
                    onAudioIconClick = testSessionViewModel::playAudioForCurrentWorkBook
                )
            }
            workBookScreenComposable(
                appContainer.workBookViewModelFactory,
                onAnswerSaved = {
                    testSessionViewModel.moveToNextWorkBook()
                    navController.popBackStack()
                }
            )
        }
    }
}

private fun NavGraphBuilder.workBookScreenComposable(
    workBookViewModelFactory: WorkBookViewModelFactory,
    onAnswerSaved: () -> Unit,
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
                    onAnswerSaved()
                }
            )
        }
    }
}