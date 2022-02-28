package com.example.examer.viewmodels.profileScreenViewModel

import android.app.Application
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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.EmptyCoroutineContext

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

    fun updateProfilePicture(
        imageBitmap: ImageBitmap,
        resetStateTimeOut: Long = defaultResetStateTimeOut
    )

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
        val currentUser = authenticationService.currentUser.value
        if (currentUser == null) {
            Timber.w("The currently logged in user is null")
            return
        }
        runUpdate {
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
            when (result) {
                is AuthenticationResult.Success -> ProfileScreenViewModel.UiState.UPDATE_SUCCESS
                is AuthenticationResult.Failure -> ProfileScreenViewModel.UiState.UPDATE_FAILURE
            }
        }
    }

    /**
     * Used to update the profile picture of the currently logged
     * in user to the [imageBitmap]. The [resetStateTimeOut] is used
     * to specify the timeout after which the [uiState] will be set back
     * to [ProfileScreenViewModel.UiState.IDLE].
     */
    override fun updateProfilePicture(imageBitmap: ImageBitmap, resetStateTimeOut: Long) {
        authenticationService.currentUser.value?.let { user ->
            runUpdate {
                repository.saveProfilePictureForUser(user, imageBitmap.asAndroidBitmap())
                ProfileScreenViewModel.UiState.UPDATE_SUCCESS
            }
        }
    }

    /**
     * A utility function that simplifies the managing of [uiState] when
     * an update is to be performed.
     *
     * The function will automatically set the [uiState] to
     * [ProfileScreenViewModel.UiState.LOADING] before the
     * [updateBlock] is executed.
     *
     * If the block executes without any exception, then the
     * [ProfileScreenViewModel.UiState] returned by the [updateBlock]
     * will be set as the value of the [uiState] before resetting the
     * [uiState] back to [ProfileScreenViewModel.UiState.IDLE] after
     * the specified [resetStateTimeOut].
     *
     * If the update block throws an exception that is not an
     * instance of [CancellationException], then the [uiState]
     * will be set to [ProfileScreenViewModel.UiState.UPDATE_FAILURE]
     * before resetting it back to [ProfileScreenViewModel.UiState.IDLE]
     * after the specified [resetStateTimeOut].If the update block
     * throws an instance of [CancellationException], it will be
     * re-thrown.
     *
     * @param coroutineExceptionHandler the exception handler for the
     * coroutines launched using [viewModelScope].
     * @param onFailed the callback to execute in the case of an
     * exception. The lambda receives an instance of [Exception].
     * @param resetStateTimeOut the amount of time in millis before
     * the [uiState] is set back to [ProfileScreenViewModel.UiState.IDLE].
     * @param updateBlock the block that is to be executed in order to
     * perform the update operation.
     */
    private fun runUpdate(
        coroutineExceptionHandler: CoroutineExceptionHandler? = null,
        onFailed: (Exception) -> Unit = {},
        resetStateTimeOut: Long = defaultResetStateTimeOut,
        updateBlock: suspend () -> ProfileScreenViewModel.UiState,
    ) {
        viewModelScope.launch(coroutineExceptionHandler ?: EmptyCoroutineContext) {
            _uiState.value = ProfileScreenViewModel.UiState.LOADING
            try {
                val uiState = updateBlock()
                resetUiStateToIdleAfterTimeOut(
                    currentUiState = uiState,
                    timeOut = resetStateTimeOut
                )
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                onFailed(exception)
                resetUiStateToIdleAfterTimeOut(
                    currentUiState = ProfileScreenViewModel.UiState.UPDATE_FAILURE,
                    timeOut = resetStateTimeOut
                )
            }
        }
    }

    /**
     * A helper method that will set the [uiState] to the [currentUiState]
     * and reset it back to [ProfileScreenViewModel.UiState.IDLE] after the
     * specified [timeOut].
     */
    private suspend fun resetUiStateToIdleAfterTimeOut(
        currentUiState: ProfileScreenViewModel.UiState,
        timeOut: Long = defaultResetStateTimeOut
    ) {
        _uiState.value = currentUiState
        delay(timeOut)
        _uiState.value = ProfileScreenViewModel.UiState.IDLE
    }
}