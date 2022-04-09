package com.example.examer.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import com.example.examer.ui.components.AlertDialog

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import coil.annotation.ExperimentalCoilApi
import com.example.examer.R
import com.example.examer.di.AppContainer
import com.example.examer.di.ExamerApplication
import com.example.examer.ui.screens.ExamerApp
import com.example.examer.ui.theme.ExamerTheme
import com.example.examer.utils.isDeviceAutomaticDateTimeEnabled
import com.example.examer.utils.isDeviceAutomaticTimeZoneEnabled
import com.example.examer.viewmodels.ExamerMainActivityViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.pager.ExperimentalPagerApi

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer
    private var isAutomaticDateTimeEnabled by mutableStateOf(false)
    private var isAutomaticTimeZoneEnabled by mutableStateOf(false)
    private val mainActivityViewModel by viewModels<ExamerMainActivityViewModel> { appContainer.mainActivityViewModelFactory }

    @ExperimentalCoilApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = (application as ExamerApplication).appContainer
        appContainer.authenticationService.currentUser.observe(this) { user ->
            // FIXME, when the user object is modified, ie name,email modification
            //  this lambda will be executed everytime. Fix that.
            // TODO delete token after user signs out
            user?.let(mainActivityViewModel::associateNotificationTokenWithUser)
        }
        setContent {
            ExamerTheme {
                ProvideWindowInsets {
                    if (!isAutomaticDateTimeEnabled || !isAutomaticTimeZoneEnabled) {
                        DateTimeZoneAlertDialog()
                    }
                    Surface(
                        color = MaterialTheme.colors.background,
                        content = { ExamerApp(appContainer) }
                    )
                }
            }
        }
    }

    @Composable
    private fun DateTimeZoneAlertDialog() {
        AlertDialog(
            title = stringResource(R.string.label_device_time_set_manually_warning),
            message = stringResource(R.string.label_device_time_set_manually_message),
            confirmText = stringResource(R.string.button_label_open_settings).uppercase(),
            onConfirmButtonClick = {
                val intent = Intent(android.provider.Settings.ACTION_DATE_SETTINGS)
                startActivity(intent)
            },
            onDismissRequest = { /*Prevent user from closing the dialog*/ }
        )
    }

    override fun onStart() {
        super.onStart()
        isAutomaticDateTimeEnabled = isDeviceAutomaticDateTimeEnabled()
        isAutomaticTimeZoneEnabled = isDeviceAutomaticTimeZoneEnabled()
    }
}

