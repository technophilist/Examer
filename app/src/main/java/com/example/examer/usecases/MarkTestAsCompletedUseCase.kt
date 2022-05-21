package com.example.examer.usecases

import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository

/**
 * An interface that specifies the requisite methods for a
 * concrete implementation of [MarkTestAsCompletedUseCase].
 */
interface MarkTestAsCompletedUseCase {
    suspend fun invoke(testDetailsId: String)
}

/**
 * A concrete implementation of [MarkTestAsCompletedUseCase].
 * @param authenticationService an instance of [AuthenticationService]
 * that will be used to get the [AuthenticationService.currentUser].
 * @param repository an instance of [Repository] that will be used to
 * mark the test as complete.
 */
class ExamerMarkTestAsCompletedUseCase(
    private val authenticationService: AuthenticationService,
    private val repository: Repository
) : MarkTestAsCompletedUseCase {
    override suspend fun invoke(testDetailsId: String) {
        authenticationService.currentUser.value?.let { user ->
            repository.markTestAsCompleted(user, testDetailsId)
        }
    }
}