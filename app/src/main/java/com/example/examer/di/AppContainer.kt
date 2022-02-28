package com.example.examer.di

import android.app.Application
import com.example.examer.auth.FirebaseAuthenticationService
import com.example.examer.data.ExamerRepository
import com.example.examer.data.Repository
import com.example.examer.data.remote.FirebaseRemoteDatabase
import com.example.examer.data.remote.RemoteDatabase
import com.example.examer.usecases.ExamerCredentialsValidationUseCase
import com.example.examer.usecases.UpdateProfilePhotoUriUseCaseImpl
import com.example.examer.utils.*
import com.example.examer.viewmodels.TestDetailsListType

class AppContainer(application: Application) {
    private val remoteDatabase =
        FirebaseRemoteDatabase(StandardDispatchersProvider()) as RemoteDatabase
    private val passwordManager = ExamerPasswordManager(application) as PasswordManager
    val authenticationService = FirebaseAuthenticationService()
    private val repository = ExamerRepository(
        remoteDatabase = remoteDatabase,
        updateProfilePhotoUriUseCase = UpdateProfilePhotoUriUseCaseImpl(
            authenticationService,
            passwordManager
        )
    ) as Repository
    val logInViewModelFactory = LogInViewModelFactory(authenticationService, passwordManager)
    val signUpViewModelFactory = SignUpViewModelFactory(
        authenticationService,
        ExamerCredentialsValidationUseCase()
    )
    val profileScreenViewModelFactory = ProfileScreenViewModelFactory(
        application,
        repository,
        authenticationService,
        passwordManager,
        credentialsValidationUseCase = ExamerCredentialsValidationUseCase()
    )
    val scheduledTestsViewModelFactory = TestsViewModelFactory(
        authenticationService = authenticationService,
        repository = repository,
        testDetailsListType = TestDetailsListType.SCHEDULED_TESTS
    )
    val previousTestsViewModelFactory = TestsViewModelFactory(
        authenticationService = authenticationService,
        repository = repository,
        testDetailsListType = TestDetailsListType.PREVIOUS_TESTS
    )
}

