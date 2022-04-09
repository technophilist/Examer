package com.example.examer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.examer.data.Repository
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.preferences.PreferencesManager
import com.example.examer.data.workers.SaveNotificationTokenWorker
import com.example.examer.data.workers.SaveNotificationTokenWorker.Companion.KEY_NOTIFICATION_TOKEN

interface MainActivityViewModel {
    fun associateNotificationTokenWithUser(user: ExamerUser)
}

class ExamerMainActivityViewModel(
    private val preferencesManager: PreferencesManager,
    application: Application
) : AndroidViewModel(application), MainActivityViewModel {
    private val workManager = WorkManager.getInstance(application)

    override fun associateNotificationTokenWithUser(user: ExamerUser) {
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
}