package com.example.examer.viewmodels

import android.net.Uri
import android.util.Patterns
import androidx.annotation.MainThread
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examer.auth.AuthenticationResult
import com.example.examer.auth.AuthenticationResult.FailureType.AccountCreation
import com.example.examer.auth.AuthenticationResult.FailureType.UserCollision
import com.example.examer.auth.AuthenticationService
import com.example.examer.di.DispatcherProvider
import com.example.examer.di.StandardDispatchersProvider
import com.example.examer.usecases.CredentialsValidationUseCase
import com.example.examer.usecases.ExamerCredentialsValidationUseCase
import com.example.examer.utils.containsDigit
import com.example.examer.utils.containsLowercase
import com.example.examer.utils.containsUppercase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * An enum class used to model the different UI states associated with
 * a SignUp screen.
 */
enum class SignUpUiFailureType {
    INVALID_CREDENTIALS,
    USER_COLLISION,
    NETWORK_ERROR,
}

/**
 * A sealed class representing the different UI states associated
 * with a SignUpScreen.
 */
sealed class SignUpUiState {
    object Loading : SignUpUiState()
    object SignedOut : SignUpUiState()
    data class Failed(val cause: SignUpUiFailureType) : SignUpUiState()
}

/**
 * An interface that consists of all the fields and methods required
 * for a SignUpViewModel.
 */
interface SignUpViewModel {
    val uiState: State<SignUpUiState>

    /**
     * Used to create a new user account based on the provided [name],
     * [email],[password] and optional [profilePhotoUri]. The [onSuccess]
     * callback will be called in the event of a successful account
     * creation. In the case of a failure, the [uiState]'s value will
     * be set appropriately.
     */
    fun createNewAccount(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        profilePhotoUri: Uri? = null
    )

    /**
     * Used to change the [uiState] to a non-error state thereby
     * removing any associated error messages from the UI layer.
     *
     * This is mainly used to hide any error messages/highlighting
     * from the associated screen that's meant to be displayed
     * when the current state is one of the error
     * states - [SignUpUiFailureType.INVALID_CREDENTIALS] or
     * [SignUpUiFailureType.NETWORK_ERROR].
     */
    fun removeErrorMessage()
}

/**
 * This is class represents a viewModel for a Sign-up screen.
 *
 * @param authenticationService the authentication service to be used.
 * @param dispatcherProvider the dispatcher provider that is to
 * be used in the viewModel. By default, it uses an instance of
 * [StandardDispatchersProvider]. The io dispatcher is changed to
 * [Dispatchers.Main] because, the data layer ensures that all
 * suspend functions are main safe.
 */
class ExamerSignUpViewModel(
    private val authenticationService: AuthenticationService,
    private val credentialsValidationUseCase: CredentialsValidationUseCase,
    private val dispatcherProvider: DispatcherProvider = StandardDispatchersProvider(io = Dispatchers.Main)
) : ViewModel(), SignUpViewModel, CredentialsValidationUseCase by credentialsValidationUseCase {
    private val _uiState = mutableStateOf<SignUpUiState>(SignUpUiState.SignedOut)
    override val uiState = _uiState as State<SignUpUiState>

    override fun createNewAccount(
        name: String,
        email: String,
        password: String,
        @MainThread onSuccess: () -> Unit,
        profilePhotoUri: Uri?,
    ) {
        if (!isValidEmail(email) || !isValidPassword(password)) _uiState.value =
            SignUpUiState.Failed(SignUpUiFailureType.INVALID_CREDENTIALS)
        else viewModelScope.launch(dispatcherProvider.io) {
            _uiState.value = SignUpUiState.Loading
            val authenticationResult =
                authenticationService.createAccount(name, email.trim(), password, profilePhotoUri)
            when (authenticationResult) {
                is AuthenticationResult.Success -> withContext(dispatcherProvider.main) { onSuccess() }
                is AuthenticationResult.Failure -> _uiState.value =
                    getUiStateForFailureType(authenticationResult.failureType)
            }
        }
    }

    override fun removeErrorMessage() {
        if (_uiState.value is SignUpUiState.Failed) {
            _uiState.value = SignUpUiState.SignedOut
        }
    }

    /**
     * Helper method used to get an instance of the associated
     * [SignUpUiState] for the provided [failureType].
     */
    private fun getUiStateForFailureType(failureType: AuthenticationResult.FailureType): SignUpUiState =
        SignUpUiState.Failed(
            when (failureType) {
                AuthenticationResult.FailureType.InvalidPassword, AuthenticationResult.FailureType.InvalidCredentials, AuthenticationResult.FailureType.InvalidEmail, AuthenticationResult.FailureType.InvalidUser -> SignUpUiFailureType.INVALID_CREDENTIALS
                AuthenticationResult.FailureType.NetworkFailure -> SignUpUiFailureType.NETWORK_ERROR
                UserCollision, AccountCreation -> SignUpUiFailureType.USER_COLLISION
            }
        )

}