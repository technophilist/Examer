package com.example.examer.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.examer.di.ExamerApplication
import kotlinx.coroutines.CancellationException

class SaveNotificationTokenWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    private val appContainer = (appContext as ExamerApplication).appContainer
    private val repository = appContainer.repository
    private val authenticationService = appContainer.authenticationService
    override suspend fun doWork(): Result = authenticationService.currentUser.value?.let { user ->
        try {
            val notificationToken = inputData.getString(KEY_NOTIFICATION_TOKEN)!!
            repository.saveNotificationTokenForUser(user, notificationToken)
            Result.success()
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure()
        }
    } ?: Result.failure()

    companion object {
        const val KEY_NOTIFICATION_TOKEN =
            "com.example.examer.data.workers.SaveNotificationTokenWorker"
    }
}