package com.example.examer.ui.screens

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.examer.R


object TakeTestScreenComposableAlertDialogBoxes {
    private val resources @Composable get() = LocalContext.current.resources

    // Alert dialog to be displayed when the timer runs out.
    @Composable
    fun TestSessionTimedOutAlertDialog(onConfirmButtonClick: () -> Unit) {
        AlertDialog(
            title = { Text(text = resources.getString(R.string.alert_dialog_label_test_timed_out)) },
            text = { Text(text = resources.getString(R.string.label_test_timed_out_message)) },
            onDismissRequest = { /* Prevent user from dismissing this dialog */ },
            confirmButton = {
                TextButton(
                    onClick = onConfirmButtonClick,
                    content = { Text(text = stringResource(R.string.button_label_quit).uppercase()) }
                )
            }
        )
    }

    // Alert dialog to be displayed when the user clicks on the finish test button.
    @Composable
    fun FinishTestAlertDialog(
        onDismissRequest: () -> Unit,
        onConfirmButtonClick: () -> Unit,
        onDismissButtonClick: () -> Unit
    ) {
        AlertDialog(
            title = { Text(text = stringResource(R.string.alert_dialog_label_end_test)) },
            text = { Text(text = stringResource(R.string.alert_dialog_label_end_test_warning)) },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onConfirmButtonClick,
                    content = { Text(text = stringResource(R.string.button_label_end_test).uppercase()) }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissButtonClick,
                    content = { Text(text = stringResource(R.string.alert_dialog_button_label_cancel).uppercase()) }
                )
            }
        )
    }

    // Alert dialog to be displayed when the user is exiting the test using the appbar button.
    @Composable
    fun QuitTestAlertDialog(
        onConfirmButtonClick: () -> Unit,
        onDismissButtonClick: () -> Unit,
        onDismissRequest: () -> Unit
    ) {
        AlertDialog(
            title = { Text(text = resources.getString(R.string.alert_dialog_label_quit_app_while_taking_test)) },
            text = { Text(text = resources.getString(R.string.alert_dialog_label_quit_test_using_back_button_warning)) },
            confirmButton = {
                TextButton(
                    onClick = onConfirmButtonClick,
                    content = { Text(text = stringResource(R.string.button_label_quit).uppercase()) }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissButtonClick,
                    content = {
                        Text(text = stringResource(R.string.alert_dialog_button_label_cancel).uppercase())
                    }
                )
            },
            onDismissRequest = onDismissRequest
        )
    }

    // Alert dialog to display when the user is clicking the back button while taking test.
    @Composable
    fun ExitAlertDialog(
        onConfirmButtonClick: () -> Unit,
        onDismissButtonClick: () -> Unit,
        onDismissRequest: () -> Unit
    ) {
        AlertDialog(
            title = { Text(text = resources.getString(R.string.alert_dialog_label_exit_test)) },
            text = {
                Text(text = resources.getString(R.string.alert_dialog_label_exit_test_warning))
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmButtonClick,
                    content = { Text(text = stringResource(R.string.button_label_quit).uppercase()) }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissButtonClick,
                    content = {
                        Text(text = stringResource(R.string.alert_dialog_button_label_cancel).uppercase())
                    }
                )
            },
            onDismissRequest = onDismissRequest
        )
    }

}