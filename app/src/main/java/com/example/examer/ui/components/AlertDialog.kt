package com.example.examer.ui.components

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.AlertDialog

@Composable
fun AlertDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirmButtonClick: () -> Unit,
    dismissButtonText: String,
    onDismissButtonClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(
                onClick = onConfirmButtonClick,
                content = { Text(text = confirmText) }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismissButtonClick,
                content = { Text(text = dismissButtonText) }
            )
        },
        onDismissRequest = onDismissRequest
    )
}