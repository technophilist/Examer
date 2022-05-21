package com.example.examer.ui.screens

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
    val isLastWorkBook by derivedStateOf {
        testSessionViewModel.currentWorkBookNumber.value == testDetails.totalNumberOfWorkBooks
    }
    val timerAndExitIconRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${testSessionViewModel.hoursRemaining.value} : ${testSessionViewModel.minutesRemaining.value} : ${testSessionViewModel.secondsRemaining.value}")
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
                        testSessionViewModel.currentWorkBookNumber.value,
                        testDetails.totalNumberOfWorkBooks
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
                questionList = multiChoiceQuestionList,
                onFooterButtonClick = { answersMap ->
                    viewModel.saveUserAnswersForTestId(
                        multiChoiceQuestionList,
                        answersMap,
                        testDetailsId,
                        workBookId
                    )
                    onAnswerSaved()
                },
                buttonTextValue = if (isLastWorkBook) ButtonTextValue.FINISH_TEST
                else ButtonTextValue.NEXT_WORKBOOK
            )
        }
    }
}