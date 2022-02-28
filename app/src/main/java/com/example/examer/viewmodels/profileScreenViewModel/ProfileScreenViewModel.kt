package com.example.examer.viewmodels.profileScreenViewModel

import android.app.Application
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.examer.auth.AuthenticationResult
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.usecases.CredentialsValidationUseCase
import com.example.examer.utils.PasswordManager
import com.example.examer.viewmodels.profileScreenViewModel.ProfileScreenViewModel.UpdateAttribute.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

const val defaultResetStateTimeOut = 4_000L

interface ProfileScreenViewModel {
    enum class UpdateAttribute { NAME, EMAIL, PASSWORD }
    enum class UiState { UPDATE_SUCCESS, UPDATE_FAILURE, LOADING, IDLE }

    val uiState: State<UiState>
    fun updateAttributeForCurrentUser(
        updateAttribute: UpdateAttribute,
        newValue: String,
        resetStateTimeOut: Long = defaultResetStateTimeOut
    )

    fun updateProfilePicture(imageBitmap: ImageBitmap)
    fun isValidEmail(email: String): Boolean
    fun isValidPassword(password: String): Boolean
}

class ExamerProfileScreenViewModel(
    application: Application,
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
    private val passwordManager: PasswordManager,
    private val credentialsValidationUseCase: CredentialsValidationUseCase,
) : AndroidViewModel(application), ProfileScreenViewModel,
    CredentialsValidationUseCase by credentialsValidationUseCase {

    private val _uiState = mutableStateOf(ProfileScreenViewModel.UiState.IDLE)
    override val uiState: State<ProfileScreenViewModel.UiState> = _uiState

    /**
     * Used to update the specified [updateAttribute] to the [newValue]
     * for the currently logged in user. The [resetStateTimeOut] is used
     * to specify the timeout after which the [uiState] will be set back
     * to [ProfileScreenViewModel.UiState.IDLE].
     */
    override fun updateAttributeForCurrentUser(
        updateAttribute: ProfileScreenViewModel.UpdateAttribute,
        newValue: String,
        resetStateTimeOut: Long
    ) {
        viewModelScope.launch {
            // TODO Remove non null assertion
            val currentUser = authenticationService.currentUser.value!!
            // set the ui state to loading
            _uiState.value = ProfileScreenViewModel.UiState.LOADING
            try {
                // update the attribute using authentication service
                val result = authenticationService.updateAttributeForUser(
                    currentUser,
                    updateAttributeType = when (updateAttribute) {
                        NAME -> AuthenticationService.UpdateAttributeType.NAME
                        EMAIL -> AuthenticationService.UpdateAttributeType.EMAIL
                        PASSWORD -> AuthenticationService.UpdateAttributeType.PASSWORD
                    },
                    newValue = newValue,
                    password = passwordManager.getPasswordForUser(currentUser) // can throw exception
                )
                // set the ui state based on the result
                _uiState.value = when (result) {
                    is AuthenticationResult.Failure -> ProfileScreenViewModel.UiState.UPDATE_FAILURE
                    is AuthenticationResult.Success -> ProfileScreenViewModel.UiState.UPDATE_SUCCESS
                }
                // set the state back to IDLE after the specified timeout
                delay(resetStateTimeOut)
                _uiState.value = ProfileScreenViewModel.UiState.IDLE
            } catch (exception: IllegalArgumentException) {
                // indicates that the PasswordManager#getPasswordForUser()
                // threw an exception because the password of the current
                // user was not saved using the password manager.
                _uiState.value = ProfileScreenViewModel.UiState.UPDATE_FAILURE
            }
        }
    }

    override fun updateProfilePicture(imageBitmap: ImageBitmap) {
        // TODO this method has not been tested
        authenticationService.currentUser.value?.let { user ->
            // set the ui state to LOADING
            _uiState.value = ProfileScreenViewModel.UiState.LOADING
            viewModelScope.launch {
                try {
                    repository.saveProfilePictureForUser(user, imageBitmap.asAndroidBitmap())
                    // if the profile picture was successfully saved, update
                    // the UI state to UPDATE_SUCCESS
                    _uiState.value = ProfileScreenViewModel.UiState.UPDATE_SUCCESS
                } catch (exception: Exception) {
                    if (exception is CancellationException) throw exception
                    // if an exception occurred set UI state to UPDATE_FAILURE
                    _uiState.value = ProfileScreenViewModel.UiState.UPDATE_FAILURE
                }
            }
        }
    }
}