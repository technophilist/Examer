package com.example.examer.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.di.DispatcherProvider
import com.example.examer.di.StandardDispatchersProvider
import com.example.examer.viewmodels.ExamerTestsViewModel
import com.example.examer.viewmodels.ExamerLogInViewModel
import com.example.examer.viewmodels.ExamerSignUpViewModel
import com.example.examer.viewmodels.TestDetailsListType
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
    private val dispatcherProvider: DispatcherProvider = StandardDispatchersProvider(io = Dispatchers.Main)
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ExamerLogInViewModel(
            authenticationService = authenticationService,
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
    private val dispatcherProvider: DispatcherProvider = StandardDispatchersProvider(io = Dispatchers.Main)
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ExamerSignUpViewModel(authenticationService, dispatcherProvider) as T
}

class TestsViewModelFactory(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
    private val testDetailsListType: TestDetailsListType
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ExamerTestsViewModel(authenticationService, repository, testDetailsListType) as T
}