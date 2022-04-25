package com.example.examer.notifications

import com.example.examer.di.ExamerApplication
import com.google.firebase.messaging.FirebaseMessagingService

class ExamerFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        // this method will be automatically called. It is not possible
        // to ensure that a user will be logged in at the time of token
        // generation. Therefore, just store it in shared preferences.
        // Associate the new token with the user after the user has successfully
        // logged in.
        val appContainer = (application as ExamerApplication).appContainer
        appContainer.preferencesManager.saveNotificationToken(newToken)
    }
}