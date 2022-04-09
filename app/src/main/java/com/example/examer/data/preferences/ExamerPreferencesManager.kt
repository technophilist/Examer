package com.example.examer.data.preferences

import android.content.Context
import androidx.core.content.edit

class ExamerPreferencesManager(context: Context) : PreferencesManager {
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveNotificationToken(notificationToken: String) {
        sharedPreferences.edit { putString(NOTIFICATION_TOKEN_KEY, notificationToken) }
    }

    override fun getNotificationTokenIfExists(): String? =
        sharedPreferences.getString(NOTIFICATION_TOKEN_KEY, null)

    override fun clearPreferences() {
        sharedPreferences.edit { clear() }
    }

    companion object {
        const val NOTIFICATION_TOKEN_KEY =
            "com.example.examer.data.preferences.ExamerPreferencesManger.KEY_NOTIFICATION_TOKEN"
        const val SHARED_PREFS_NAME = "examer_shared_prefs"
    }
}