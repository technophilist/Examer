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
     * The [ExamerUser.id] of the [examerUser] will be used as the key
     * to get the value of the password.
     * @throws IllegalArgumentException if there is no password associated
     * with the user.
     */
    override fun getPasswordForUser(examerUser: ExamerUser): String {
        if (encryptedSharedPreferences.contains(examerUser.id)) {
            return encryptedSharedPreferences.getString(examerUser.id, null)!!
        }
        throw IllegalArgumentException("The password for the user ${examerUser.name} does not exist.")
    }

    /**
     * Used to securely save the [password] of the [examerUser].
     *
     * The [ExamerUser.id] of the [examerUser] will be used as the key
     * to save the value of the password.
     */
    override fun savePasswordForUser(examerUser: ExamerUser, password: String) {
        encryptedSharedPreferences.edit {
            // since only one user can be logged in at a given time,
            // clear the shared preferences before adding a new entry
            // in order to remove the key,value pair stored for the
            // previously logged in user.
            clear()
            putString(examerUser.id, password)
        }
    }
}