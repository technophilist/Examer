package com.example.examer.notifications

import com.example.examer.di.DispatcherProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface RemoteNotificationService {
    suspend fun generateNewToken(): Result<String>
    suspend fun deleteNotificationToken()
}

class FirebaseNotificationService(
    private val dispatcherProvider: DispatcherProvider
) : RemoteNotificationService {

    override suspend fun generateNewToken(): Result<String> {
        return withContext(dispatcherProvider.io) {
            runCatching { Firebase.messaging.token.await() }
        }
    }

    override suspend fun deleteNotificationToken() {
        // can throw exception
        withContext(dispatcherProvider.io) { Firebase.messaging.deleteToken().await() }
    }
}