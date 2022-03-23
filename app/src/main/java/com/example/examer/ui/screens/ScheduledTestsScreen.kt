package com.example.examer.ui.screens

import android.text.format.DateFormat
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.example.examer.data.domain.TestDetails
import com.example.examer.ui.components.examerTestCard.DefaultExamerExpandableTestCard
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ScheduledTestsScreen(
    tests: List<TestDetails>,
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    onStartTest: (TestDetails) -> Unit
) {
    val listHeader = stringResource(id = R.string.label_upcoming_tests)
    var isAlertDialogVisible by remember {
        mutableStateOf(false)
    }
    val resources = LocalContext.current.resources
    var currentlySelectedTestDetails by remember { mutableStateOf<TestDetails?>(null) }
    val onConfirmButtonClick: () -> Unit = {
        isAlertDialogVisible = false
        currentlySelectedTestDetails?.let(onStartTest)
    }
    if (isAlertDialogVisible) {
        AlertDialog(
            title = {
                Text(
                    text = resources.getString(R.string.label_start_test),
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(text = resources.getString(R.string.label_start_test_message)) },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(text = resources.getString(R.string.button_label_start_test).uppercase())
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isAlertDialogVisible = false }
                ) {
                    Text(
                        text = resources
                            .getString(R.string.alert_dialog_button_label_cancel)
                            .uppercase()
                    )
                }
            },
            onDismissRequest = { isAlertDialogVisible = false }
        )
    }
    TestListScreen(
        listHeader = listHeader,
        testList = tests,
        swipeRefreshState = swipeRefreshState,
        onRefresh = onRefresh
    ) { testDetailsItem, isExpanded, onExpandButtonClick, onClick, is24hourFormat ->
        DefaultExamerExpandableTestCard(
            test = testDetailsItem,
            isExpanded = isExpanded,
            onExpandButtonClick = onExpandButtonClick,
            onClick = onClick,
            is24HourTimeFormat = is24hourFormat,
            onTakeTestButtonClick = {
                currentlySelectedTestDetails = testDetailsItem
                isAlertDialogVisible = true
            }
        )
    }
}

