package com.example.examer.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.di.DispatcherProvider
import com.example.examer.di.StandardDispatchersProvider
import com.example.examer.usecases.CredentialsValidationUseCase
import com.example.examer.viewmodels.*
import com.example.examer.viewmodels.profileScreenViewModel.ExamerProfileScreenViewModel
import kotlinx.coroutines.Dispatchers

/**
 * A [ViewModelProvider.Factory] that is used for creating an
 * instance of [ExamerLogInViewModel].
 * @param authenticationService the authentication service that
 * is used in the viewModel.
 * @param dispatcherProvider the dispatcher provider that is to
 * be used in the viewModel. By default, it uses an instance of
 * [StandardDispatchersProvider]. The io dispatcher is changed to
 * [Dispatchers.Main] because, the data layer ensures that all
 * suspend functions are main safe.
 */
class LogInViewModelFactory(
    private val authenticationService: AuthenticationService,
    private val passwordManager: PasswordManager,
    private val dispatcherProvider: DispatcherProvider = StandardDispatchersProvider(io = Dispatchers.Main)
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ExamerLogInViewModel(
            authenticationService = authenticationService,
            passwordManager = passwordManager,
            dispatcherProvider = dispatcherProvider
        ) as T
}

/**
 * A [ViewModelProvider.Factory] that is used for creating an
 * instance of [ExamerSignUpViewModel].
 * @param authenticationService the authentication service that
 * is used in the viewModel.
 * @param dispatcherProvider the dispatcher provider that is to
 * be used in the viewModel. By default, it uses an instance of
 * [StandardDispatchersProvider]. The io dispatcher is changed to
 * [Dispatchers.Main] because, the data layer ensures that all
 * suspend functions are main safe.
 */
class SignUpViewModelFactory(
    private val authenticationService: AuthenticationService,
    private val passwordManager: PasswordManager,
    private val credentialsValidationUseCase: CredentialsValidationUseCase,
    private val dispatcherProvider: DispatcherProvider = StandardDispatchersProvider(io = Dispatchers.Main)
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ExamerSignUpViewModel(
            authenticationService,
            passwordManager,
            credentialsValidationUseCase,
            dispatcherProvider
        ) as T
}

/**
 * A [ViewModelProvider.Factory] that is used for creating an
 * instance of [ExamerTestsViewModel].
 * @param authenticationService the authentication service that
 * is used in the viewModel.
 * @param repository a concrete implementation of [Repository].
 * @param testDetailsListType represents what type of tests the
 * instance of the viewModel is dealing with.
 */
class TestsViewModelFactory(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
    private val testDetailsListType: TestDetailsListType
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ExamerTestsViewModel(authenticationService, repository, testDetailsListType) as T
}

/**
 * A [ViewModelProvider.Factory] that is used for creating an
 * instance of [ExamerProfileScreenViewModel].
 * @param application an instance of the application class.
 * @param repository a concrete implementation of [Repository].
 * @param authenticationService the authentication service that
 * is used in the viewModel.
 * @param passwordManager a concrete implementation of [passwordManager]
 * that will be used in the viewModel to retrieve the password.
 * @param credentialsValidationUseCase an instance of
 * [credentialsValidationUseCase] that is used to validate the
 * credentials.
 */
class ProfileScreenViewModelFactory(
    private val application: Application,
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
    private val passwordManager: PasswordManager,
    private val credentialsValidationUseCase: CredentialsValidationUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ExamerProfileScreenViewModel(
        application = application,
        repository = repository,
        authenticationService = authenticationService,
        passwordManager = passwordManager,
        credentialsValidationUseCase = credentialsValidationUseCase
    ) as T
}