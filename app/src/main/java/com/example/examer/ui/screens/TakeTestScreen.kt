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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.examer.R
import com.example.examer.data.domain.MultiChoiceQuestion
import com.example.examer.data.domain.TestDetails
import com.example.examer.di.AppContainer
import com.example.examer.ui.navigation.TakeTestScreenDestinations
import com.example.examer.ui.screens.listenToAudioScreen.AudioPlaybackState
import com.example.examer.ui.screens.listenToAudioScreen.ListenToAudioScreen
import com.example.examer.ui.screens.listenToAudioScreen.TimerState
import com.example.examer.ui.screens.listenToAudioScreen.WorkBookState
import com.example.examer.utils.WorkBookViewModelFactory
import com.example.examer.viewmodels.ExamerWorkBookViewModel
import com.example.examer.viewmodels.TestSessionViewModel
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Composable
fun TakeTestScreen(
    appContainer: AppContainer,
    testSessionViewModel: TestSessionViewModel,
    onExitTestButtonClick: () -> Unit,
    onFinishTestButtonClick: () -> Unit,
    testDetails: TestDetails,
) {
    val navController = rememberNavController()
    val workBookState = remember {
        WorkBookState(
            testSessionViewModel.currentWorkBookNumber,
            testDetails.totalNumberOfWorkBooks
        )
    }
    val isLastWorkBook by derivedStateOf {
        testSessionViewModel.currentWorkBookNumber.value == testDetails.totalNumberOfWorkBooks
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
                // TODO AudioPlayBack state api not upto the mark
                //  isAudioPlayBackEnabled and isAudioIconEnabled param
                //  of composable - two sources of truth.(Change it)
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
                val currentWorkBook by testSessionViewModel.currentWorkBook
                ListenToAudioScreen(
                    audioPlayBackState = audioPlaybackState,
                    onNavigateToWorkBook = {
                        val routeString =
                            TakeTestScreenDestinations.WorkBookScreen.buildRoute(
                                testDetails.id,
                                currentWorkBook.id,
                                currentWorkBook.questions,
                                isLastWorkBook
                            )
                        testSessionViewModel.stopAudioPlayback()
                        navController.navigate(routeString)
                    },
                    isAudioIconClickEnabled = isAudioIconClickEnabled,
                    onAudioIconClick = testSessionViewModel::playAudioForCurrentWorkBook
                )
            }
            workBookScreenComposable(
                appContainer.workBookViewModelFactory,
                onAnswerSaved = {
                    if (isLastWorkBook) {
                        onFinishTestButtonClick()
                    } else {
                        testSessionViewModel.moveToNextWorkBook()
                        navController.popBackStack()
                    }
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
            val isLastWorkBook = bundle
                .getString(TakeTestScreenDestinations.WorkBookScreen.IS_LAST_WORKBOOK_ARG)!!
                .toBoolean()
            val viewModel = viewModel<ExamerWorkBookViewModel>(
                factory = workBookViewModelFactory,
                viewModelStoreOwner = it
            )
            WorkBookScreen(
                workBookId = workBookId,
                questionList = multiChoiceQuestionList,
                onFooterButtonClick = { userAnswers ->
                    viewModel.saveUserAnswersForTestId(userAnswers, testDetailsId)
                    onAnswerSaved()
                },
                buttonTextValue = if (isLastWorkBook) ButtonTextValue.FINISH_TEST
                else ButtonTextValue.NEXT_WORKBOOK
            )
        }
    }
}