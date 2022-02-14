package com.example.examer.auth

import android.net.Uri
import com.example.examer.data.domain.ExamerUser
import com.google.firebase.auth.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * A concrete implementation of [AuthenticationService] that makes use
 * of Firebase.
 */
class FirebaseAuthenticationService(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthenticationService {

    private val firebaseAuth = FirebaseAuth.getInstance()
    override val currentUser get() = firebaseAuth.currentUser?.toExamerUser()

    /**
     * Used to signIn an existing user with the specified [email] and
     * [password].
     * @return an instance of [AuthenticationResult.Failure] if an error
     * occurred, or, an instance of [AuthenticationResult.Success] if
     * an account was created successfully.
     */
    override suspend fun signIn(
        email: String,
        password: String
    ): AuthenticationResult = withContext(defaultDispatcher) {
        runCatching {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthenticationResult.Success(firebaseAuth.currentUser!!.toExamerUser())
        }.getOrElse {
            AuthenticationResult.Failure(
                when (it) {
                    is FirebaseAuthInvalidUserException -> AuthenticationResult.FailureType.InvalidEmail
                    is FirebaseAuthInvalidCredentialsException -> AuthenticationResult.FailureType.InvalidPassword
                    else -> AuthenticationResult.FailureType.NetworkFailure
                }
            )
        }
    }

    /**
     * Used to create a new user account with the specified [username],
     * [email],[password] and [profilePhotoUri].
     * @return an instance of [AuthenticationResult.Failure] if an error
     * occurred, or, an instance of [AuthenticationResult.Success] if
     * an account was created successfully.
     */
    override suspend fun createAccount(
        username: String,
        email: String,
        password: String,
        profilePhotoUri: Uri?
    ): AuthenticationResult = withContext(defaultDispatcher) {
        runCatching {
            val firebaseUser = firebaseAuth.createUser(username, email, password, profilePhotoUri)
            AuthenticationResult.Success(firebaseUser.toExamerUser())
        }.getOrElse {
            AuthenticationResult.Failure(
                when (it) {
                    is FirebaseAuthWeakPasswordException -> AuthenticationResult.FailureType.InvalidPassword
                    is FirebaseAuthInvalidCredentialsException -> AuthenticationResult.FailureType.InvalidCredentials
                    is FirebaseAuthUserCollisionException -> AuthenticationResult.FailureType.UserCollision
                    is FirebaseAuthInvalidUserException -> AuthenticationResult.FailureType.InvalidUser
                    else -> AuthenticationResult.FailureType.NetworkFailure
                }
            )
        }
    }

    /**
     * This method is used for signing-out the current signed-in user.
     */
    override fun signOut() {
        firebaseAuth.signOut()
    }

    /**
     * Utility method to convert an instance of [FirebaseUser] to
     * [ExamerUser]
     */
    private fun FirebaseUser.toExamerUser() = ExamerUser(
        id = uid,
        name = displayName ?: "",
        email = email!!,
        phoneNumber = phoneNumber,
        photoUrl = photoUrl
    )
}
