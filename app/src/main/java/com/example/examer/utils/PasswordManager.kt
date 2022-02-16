package com.example.examer.utils

import android.app.Application
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.examer.data.domain.ExamerUser
import java.lang.IllegalArgumentException

/**
 * An interface that specifies the methods that are required for a
 * concrete implementation of [PasswordManager].
 */
interface PasswordManager {
    fun getPasswordForUser(examerUser: ExamerUser): String
    fun savePasswordForUser(examerUser: ExamerUser, password: String)
}

/**
 * A concrete implementation of [PasswordManager] that uses
 * [EncryptedSharedPreferences] under the hood.
 */
class ExamerPasswordManager(application: Application) : PasswordManager {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val encryptedSharedPreferencesFileName = "espf"
    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        encryptedSharedPreferencesFileName,
        masterKeyAlias,
        application,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Used to get the password associated with the [examerUser].
     * The hashcode of the [examerUser] will be used as the key
     * to get the value of the password.
     * @throws IllegalArgumentException if there is no password associated
     * with the user.
     */
    override fun getPasswordForUser(examerUser: ExamerUser): String {
        if (encryptedSharedPreferences.contains(examerUser.hashCode().toString())) {
            return encryptedSharedPreferences.getString(examerUser.hashCode().toString(), null)!!
        }
        throw IllegalArgumentException("There password for the user ${examerUser.name} does not exist")
    }

    /**
     * Used to securely save the [password] of the [examerUser].
     */
    override fun savePasswordForUser(examerUser: ExamerUser, password: String) {
        encryptedSharedPreferences.edit { putString(examerUser.hashCode().toString(), password) }
    }
}