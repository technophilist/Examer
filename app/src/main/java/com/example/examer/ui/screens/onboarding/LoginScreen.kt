package com.example.examer.ui.screens.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.examer.ui.components.ExamerSingleLineTextField
import com.example.examer.R
import com.example.examer.ui.components.CircularLoadingProgressOverlay
import com.example.examer.viewmodels.LogInViewModel
import com.example.examer.viewmodels.LoginUiState
import com.google.accompanist.insets.systemBarsPadding

/**
 * A stateful implementation of login screen.
 */
@ExperimentalComposeUiApi
@Composable
fun LoginScreen(
    viewModel: LogInViewModel,
    onSuccessfulAuthentication: () -> Unit
) {
    val uiState by viewModel.uiState
    var emailAddressText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val isLoginButtonEnabled by remember(
        emailAddressText,
        passwordText
    ) { mutableStateOf(emailAddressText.isNotBlank() && passwordText.isNotEmpty()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    // keyboard actions for the text fields
    val keyboardActions = KeyboardActions(onDone = {
        if (emailAddressText.isNotBlank() && passwordText.isNotEmpty()) {
            keyboardController?.hide()
            viewModel.authenticate(
                emailAddress = emailAddressText,
                password = passwordText,
                onSuccess = onSuccessfulAuthentication
            )
        }
    })

    LoginScreen(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(8.dp),
        emailAddressText = emailAddressText,
        onEmailAddressTextChange = {
            viewModel.removeErrorMessage() // If there is an error message, clear it
            emailAddressText = it
        },
        passwordText = passwordText,
        onPasswordTextChange = {
            viewModel.removeErrorMessage() // If there is an error message, clear it
            passwordText = it
        },
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityIconClick = { isPasswordVisible = !isPasswordVisible },
        onLoginButtonClick = {
            viewModel.authenticate(
                emailAddress = emailAddressText,
                password = passwordText,
                onSuccess = onSuccessfulAuthentication
            )
        },
        errorMessage = {
            Text(
                text = when (uiState) {
                    LoginUiState.NETWORK_ERROR -> stringResource(id = R.string.label_network_error_message)
                    LoginUiState.WRONG_CREDENTIALS -> stringResource(id = R.string.label_login_error_message)
                    else -> ""
                },
                color = MaterialTheme.colors.error
            )
        },
        keyboardActions = keyboardActions,
        isLoginButtonEnabled = isLoginButtonEnabled,
        isLoadingOverlayVisible = uiState == LoginUiState.LOADING,
        isErrorMessageVisible = uiState == LoginUiState.WRONG_CREDENTIALS || uiState == LoginUiState.NETWORK_ERROR,
    )
}

/**
 * A stateless implementation of login screen.
 */
@Composable
fun LoginScreen(
    emailAddressText: String,
    onEmailAddressTextChange: (String) -> Unit,
    passwordText: String,
    onPasswordTextChange: (String) -> Unit,
    onLoginButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoginButtonEnabled: Boolean = true,
    isLoadingOverlayVisible: Boolean = false,
    isErrorMessageVisible: Boolean = false,
    errorMessage: @Composable () -> Unit = {},
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityIconClick: () -> Unit = {},
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    CircularLoadingProgressOverlay(isOverlayVisible = isLoadingOverlayVisible) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .paddingFromBaseline(top = 176.dp),
                text = stringResource(id = R.string.button_label_login_with_email),
                style = MaterialTheme.typography.h3
            )

            Spacer(modifier = Modifier.padding(16.dp))

            ExamerSingleLineTextField(
                modifier = Modifier.fillMaxWidth(),
                value = emailAddressText,
                onValueChange = onEmailAddressTextChange,
                isError = isErrorMessageVisible,
                keyboardActions = keyboardActions,
                label = { Text(text = stringResource(R.string.label_email_address)) },
            )

            Spacer(modifier = Modifier.padding(8.dp))

            ExamerSingleLineTextField(
                modifier = Modifier.fillMaxWidth(),
                value = passwordText,
                onValueChange = onPasswordTextChange,
                label = { Text(text = stringResource(R.string.label_password)) },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = isErrorMessageVisible,
                trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable(onClick = onPasswordVisibilityIconClick),
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff,
                        contentDescription = ""
                    )
                },
                keyboardActions = keyboardActions,
            )
            if (isErrorMessageVisible) {
                Surface(
                    modifier = Modifier.align(Alignment.Start),
                    content = errorMessage
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(top = 24.dp),
                text = stringResource(id = R.string.label_terms_and_conditions),
                style = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Button(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = onLoginButtonClick,
                shape = MaterialTheme.shapes.medium,
                content = {
                    Text(
                        text = stringResource(id = R.string.button_label_login),
                        fontWeight = FontWeight.Bold
                    )
                },
                enabled = isLoginButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondary,
                    contentColor = MaterialTheme.colors.onSecondary
                )
            )
        }
    }
}

