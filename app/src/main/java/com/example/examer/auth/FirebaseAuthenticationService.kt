package com.example.examer.auth

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.examer.auth.AuthenticationService.*
import com.example.examer.data.domain.ExamerUser
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * A concrete implementation of [AuthenticationService] that makes use
 * of Firebase.
 */
class FirebaseAuthenticationService(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AuthenticationService {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _currentUser =
        MutableLiveData<ExamerUser?>(firebaseAuth.currentUser?.toExamerUser())
    override val currentUser: LiveData<ExamerUser?> = _currentUser

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
        // Does the run cathcing block also catch cancellation exception?
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

    override suspend fun updateAttributeForUser(
        user: ExamerUser,
        updateAttributeType: UpdateAttributeType,
        newValue: String,
        password: String
    ): AuthenticationResult = withContext(defaultDispatcher) {
        runCatching {
            val currentUser = firebaseAuth.currentUser
            when (updateAttributeType) {
                UpdateAttributeType.NAME -> currentUser?.changeUserName(newValue)
                UpdateAttributeType.EMAIL -> currentUser?.changeEmail(newValue, password)
                UpdateAttributeType.PASSWORD -> currentUser?.changePassword(newValue, password)
                UpdateAttributeType.PROFILE_PHOTO_URI -> currentUser?.changePhotoUri(
                    Uri.parse(newValue)
                )
            }
            _currentUser.postValue(firebaseAuth.currentUser!!.toExamerUser())
            AuthenticationResult.Success(firebaseAuth.currentUser!!.toExamerUser())
        }.getOrElse {
            AuthenticationResult.Failure(
                if (it is FirebaseNetworkException) AuthenticationResult.FailureType.NetworkFailure
                else AuthenticationResult.FailureType.InvalidCredentials
            )
        }
    }


    /**
     * Utility method to convert an instance of [FirebaseUser] to
     * [ExamerUser]
     */
    private fun FirebaseUser.toExamerUser() = ExamerUser(
        id = uid,
        name = displayName ?: "",
        email = email!!,
        phoneNumber = if (phoneNumber?.isEmpty() == true) null else phoneNumber,
        photoUrl = photoUrl
    )
}
