package com.example.examer.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.examer.data.preferences.ExamerPreferencesManager
import com.example.examer.data.preferences.PreferencesManager
import com.example.examer.di.ExamerApplication
import kotlinx.coroutines.CancellationException
import timber.log.Timber

class SaveNotificationTokenWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    private val appContainer = (appContext as ExamerApplication).appContainer
    private val repository = appContainer.repository
    private val authenticationService = appContainer.authenticationService
    override suspend fun doWork(): Result = authenticationService.currentUser.value?.let { user ->
        Timber.d("Working!!")
        try {
            val notificationToken = inputData.getString(KEY_NOTIFICATION_TOKEN)!!
            repository.saveNotificationTokenForUser(user, notificationToken)
            // delete the entry from shared prefs once the notification token
            // is saved.
            appContainer.preferencesManager.deleteValueIfExists(ExamerPreferencesManager.NOTIFICATION_TOKEN_KEY)
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