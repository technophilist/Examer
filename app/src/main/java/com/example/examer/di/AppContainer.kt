package com.example.examer.di

import android.app.Application
import android.media.MediaPlayer
import com.example.examer.auth.FirebaseAuthenticationService
import com.example.examer.data.ExamerRepository
import com.example.examer.data.Repository
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.WorkBook
import com.example.examer.data.remote.FirebaseRemoteDatabase
import com.example.examer.data.remote.RemoteDatabase
import com.example.examer.usecases.ExamerCredentialsValidationUseCase
import com.example.examer.usecases.ExamerMarkTestAsCompletedUseCase
import com.example.examer.delegates.ExamerUpdateProfileUriDelegate
import com.example.examer.utils.*
import com.example.examer.viewmodels.TestDetailsListType

class AppContainer(application: Application) {
    private val remoteDatabase =
        FirebaseRemoteDatabase(StandardDispatchersProvider()) as RemoteDatabase
    private val passwordManager = ExamerPasswordManager(application) as PasswordManager
    val authenticationService = FirebaseAuthenticationService()
    val repository = ExamerRepository(
        context = application,
        remoteDatabase = remoteDatabase,
        updateProfileUriDelegate = ExamerUpdateProfileUriDelegate(authenticationService)
    ) as Repository
    val logInViewModelFactory = LogInViewModelFactory(authenticationService, passwordManager)
    val signUpViewModelFactory = SignUpViewModelFactory(
        authenticationService,
        ExamerPasswordManager(application),
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
    val previousTestsViewModelFactory = PreviousTestsViewModelFactory(
        authenticationService = authenticationService,
        repository = repository
    )

    val workBookViewModelFactory = WorkBookViewModelFactory(application = application)
    fun getTestSessionViewModelFactory(
        testDetails: TestDetails,
        workBookList: List<WorkBook>
    ) = TestSessionViewModelFactory(
        mediaPlayer = MediaPlayer(),
        testDetails = testDetails,
        workBookList = workBookList,
        markTestAsCompletedUseCase = ExamerMarkTestAsCompletedUseCase(
            authenticationService,
            repository
        )
    )
}

