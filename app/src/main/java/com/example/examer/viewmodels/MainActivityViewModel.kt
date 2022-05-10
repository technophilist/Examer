package com.example.examer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.*
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.preferences.PreferencesManager
import com.example.examer.data.workers.SaveNotificationTokenWorker
import com.example.examer.data.workers.SaveNotificationTokenWorker.Companion.KEY_NOTIFICATION_TOKEN
import com.example.examer.notifications.RemoteNotificationService

interface MainActivityViewModel {
    fun associateNotificationTokenWithUser(user: ExamerUser)
    fun deleteNotificationToken()
}

class ExamerMainActivityViewModel(
    private val preferencesManager: PreferencesManager,
    application: Application
) : AndroidViewModel(application), MainActivityViewModel {
    private val workManager = WorkManager.getInstance(application)

    override fun associateNotificationTokenWithUser(user: ExamerUser) {
        // the worker will remove the entry from preferences, once
        // it has successfully associated the user with the
        // notification token. Therefore, this function will return
        // if it is called multiple times, since
        // preferencesManager.getNotificationTokenIfExists() will return
        // null if it called more than once.
        val notificationToken = preferencesManager.getNotificationTokenIfExists() ?: return
        val workerConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val inputData = Data.Builder()
            .putString(KEY_NOTIFICATION_TOKEN, notificationToken)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(SaveNotificationTokenWorker::class.java)
            .setInputData(inputData)
            .setConstraints(workerConstraints)
            .build()
        workManager.enqueue(oneTimeWorkRequest)
    }

    override fun deleteNotificationToken() {
        // TODO
    }
}