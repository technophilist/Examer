package com.example.examer.ui.screens

import androidx.compose.animation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.examer.R
import com.example.examer.data.domain.*
import com.example.examer.ui.components.examerTestCard.DefaultExamerExpandableTestCard
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.example.examer.ui.components.AlertDialog

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ScheduledTestsScreen(
    tests: List<TestDetails>,
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    onStartTest: (TestDetails) -> Unit,
    onTestExpired: (TestDetails) -> Unit
) {
    val listHeader = stringResource(id = R.string.label_upcoming_tests)
    var isStartTestAlertDialogVisible by remember { mutableStateOf(false) }
    var isTestExpiredAlertDialogVisible by remember { mutableStateOf(false) }
    var isTestNotOpenAlertDialogVisible by remember { mutableStateOf(false) }
    val resources = LocalContext.current.resources
    var currentlySelectedTestDetails by remember { mutableStateOf<TestDetails?>(null) }
    val onConfirmButtonClick: () -> Unit = {
        isStartTestAlertDialogVisible = false
        currentlySelectedTestDetails?.let(onStartTest)
    }
    if (isStartTestAlertDialogVisible) {
        AlertDialog(
            title = resources.getString(R.string.button_label_start_test),
            message = resources.getString(R.string.label_start_test_message),
            confirmText = resources.getString(R.string.button_label_start_test).uppercase(),
            onConfirmButtonClick = onConfirmButtonClick,
            dismissButtonText = resources
                .getString(R.string.alert_dialog_button_label_cancel)
                .uppercase(),
            onDismissButtonClick = { isStartTestAlertDialogVisible = false },
            onDismissRequest = { isStartTestAlertDialogVisible = false })
    }

    if (isTestExpiredAlertDialogVisible) {
        AlertDialog(
            title = stringResource(R.string.label_test_expired),
            message = stringResource(R.string.alert_dialog_label_test_expired_warning),
            confirmText = stringResource(R.string.button_label_close).uppercase(),
            onConfirmButtonClick = {
                currentlySelectedTestDetails?.let(onTestExpired)
                isTestExpiredAlertDialogVisible = false
            },
            onDismissRequest = {
                currentlySelectedTestDetails?.let(onTestExpired)
                isTestExpiredAlertDialogVisible = false
            }
        )
    }

    if (isTestNotOpenAlertDialogVisible) {
        AlertDialog(
            title = stringResource(R.string.alert_dialog_label_test_not_open),
            message = stringResource(id = R.string.alert_dialog_label_test_not_open),
            confirmText = stringResource(R.string.button_label_close).uppercase(),
            onConfirmButtonClick = { isTestNotOpenAlertDialogVisible = false },
            onDismissRequest = { isTestNotOpenAlertDialogVisible = false })
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
                when {
                    testDetailsItem.isScheduledNotOpen() -> isTestNotOpenAlertDialogVisible = true
                    testDetailsItem.isTestExpired() -> isTestExpiredAlertDialogVisible = true
                    else -> isStartTestAlertDialogVisible = true
                }
            }
        )
    }
}

