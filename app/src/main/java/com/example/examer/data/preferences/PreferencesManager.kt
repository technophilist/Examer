package com.example.examer.data.preferences

interface PreferencesManager {
    fun saveNotificationToken(notificationToken:String)
    fun getNotificationTokenIfExists():String?
    fun clearPreferences()
}