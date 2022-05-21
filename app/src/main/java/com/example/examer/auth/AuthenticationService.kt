package com.example.examer.auth

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.examer.auth.AuthenticationResult.*
import com.example.examer.data.domain.ExamerUser

/**
 * An interface that contains the requisite fields and methods required
 * for an authentication service.
 */
interface AuthenticationService {
    /**
     * A sealed class that contains multiple nested classes that model
     * different user attributes that can be updated.
     * For certain attributes, a password is required to update
     * the value. For such attributes, a password field will be defined
     * as the constructor param of the nested class.
     */
    sealed class UpdateAttribute {
        data class Name(val newName: String) : UpdateAttribute()
        data class Email(val newEmail: String, val password: String) : UpdateAttribute()
        data class Password(val newPassword: String, val oldPassword: String) : UpdateAttribute()
        data class ProfilePhotoUri(val newPhotoUri: Uri) : UpdateAttribute()
    }

    /**
     * The current user represents the user that is
     * currently logged in. If it is null, it implies
     * that there is no logged in user.
     *
     * This value is a [LiveData] instead of [ExamerUser]
     * in order to ensure that any modifications to the
     * attributes of the user are propagated to the
     * entire app.
     */
    val currentUser: LiveData<ExamerUser?>

    /***
     * Used to sign in a user with the provided [email]
     * and [password]. An instance of [AuthenticationResult] will be
     * returned to indicate a successful or failed sign-in attempt.
     */
    suspend fun signIn(email: String, password: String): AuthenticationResult

    /**
     * Used to create a new user account with the provided [username],
     * [email],[password] and an optional [profilePhotoUri].
     * An instance of [AuthenticationResult] will be returned to
     * indicate whether an account was successfully created or not.
     */
    suspend fun createAccount(
        username: String,
        email: String,
        password: String,
        profilePhotoUri: Uri? = null
    ): AuthenticationResult

    /**
     * Used to sign out the current user.
     */
    fun signOut()

    /**
     * Used to update an attribute of the specified [user].
     * The [UpdateAttribute] param of the function will
     * be used to specify the attribute to update.
     * An instance of [AuthenticationResult] will be returned to
     * indicate whether the update was successful.
     */
    suspend fun updateAttributeForUser(
        user: ExamerUser,
        updateAttribute: UpdateAttribute
    ): AuthenticationResult
}

/**
 * A sealed class that encapsulates the status of initiating the
 * authentication process.
 *
 * This sealed class consist of two data classes representing
 * success and failure states.
 * The [Success] class contains an [ExamerUser] object, which
 * represents the user who was successfully authenticated.
 * The [Failure] class contains the [FailureType] which can
 * be used to infer the type of failure.
 */
sealed class AuthenticationResult {
    data class Success(val user: ExamerUser) : AuthenticationResult()
    data class Failure(val failureType: FailureType) : AuthenticationResult()

    /**
     * An enum consisting of all the different types of failures
     * related to [AuthenticationService]
     */
    enum class FailureType {
        /**
         * Indicates that an authentication failure occurred due to
         * an invalid email address.
         */
        InvalidEmail,

        /**
         * Indicates that an authentication failure occurred due to
         * an invalid password.
         */
        InvalidPassword,

        /**
         * Indicates that an authentication failure occurred as a
         * result of one or more of the credentials being invalid.
         */
        InvalidCredentials,

        /**
         * Indicates that an authentication failure occurred
         * as a result of an attempt to create an already existing
         * user with the same credentials.
         */
        UserCollision,

        /**
         * Indicates that an authentication failure occurred
         * during the creation of a new user account.
         */
        AccountCreation,

        /**
         * Indicates that an authentication failure occurred as
         * a result of an attempt made to fetch the details
         * of a non-existent user.
         */
        InvalidUser,

        /**
         * Indicates that a failure occurred due to a network
         * error.
         */
        NetworkFailure
    }
}
