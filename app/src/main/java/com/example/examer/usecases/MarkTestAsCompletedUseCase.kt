package com.example.examer.usecases

import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository

interface MarkTestAsCompletedUseCase {
    suspend fun invoke(testDetailsId: String)
}

class ExamerMarkTestAsCompletedUseCase(
    private val authenticationService: AuthenticationService,
    private val repository: Repository
):MarkTestAsCompletedUseCase {
    override suspend fun invoke(testDetailsId: String) {
        authenticationService.currentUser.value?.let { user ->
            repository.markTestAsCompleted(user, testDetailsId)
        }
    }
}