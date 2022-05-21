package com.example.examer.auth

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.examer.auth.AuthenticationService.*
import com.example.examer.data.domain.ExamerUser
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import kotlinx.coroutines.CancellationException
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

    override suspend fun signIn(
        email: String,
        password: String
    ): AuthenticationResult = withContext(defaultDispatcher) {
        runCatching {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()!!
            val examerUser = authResult.user!!.toExamerUser()
            // there will be a small moment in time, wherein
            // the livedata will be null even after the user
            // is authenticated. This is the time interval
            // between the user getting authenticated
            // and when the new user object is actually assigned
            // to the _currentUser live data.
            // Assign the examer user to the current user live data
            // immediately, in order to ensure that the value of
            // the live data is not null even after the user
            // is authenticated.
            _currentUser.postValue(examerUser)
            AuthenticationResult.Success(examerUser)
        }.getOrElse {
            Timber.d(it)
            AuthenticationResult.Failure(
                when (it) {
                    is FirebaseAuthInvalidUserException -> AuthenticationResult.FailureType.InvalidEmail
                    is FirebaseAuthInvalidCredentialsException -> AuthenticationResult.FailureType.InvalidPassword
                    else -> AuthenticationResult.FailureType.NetworkFailure
                }
            )
        }
    }

    override suspend fun createAccount(
        username: String,
        email: String,
        password: String,
        profilePhotoUri: Uri?
    ): AuthenticationResult = withContext(defaultDispatcher) {
        // Does the run cathcing block also catch cancellation exception?
        runCatching {
            val user = firebaseAuth.createUser(username, email, password, profilePhotoUri)
                .toExamerUser()
            // there will be a small moment in time, wherein
            // the livedata will be null even after the user
            // is authenticated. This is the time interval
            // between the user getting authenticated
            // and when the new user object is actually assigned
            // to the _currentUser live data.
            // Assign the examer user to the current user live data
            // immediately, in order to ensure that the value of
            // the live data is not null even after the user
            // is authenticated.
            _currentUser.postValue(user)
            AuthenticationResult.Success(user)
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

    override fun signOut() {
        firebaseAuth.signOut()
        // manually set the current user to null
        // else, the user will only be null
        // after a cold start.
        _currentUser.value = null
    }

    override suspend fun updateAttributeForUser(
        user: ExamerUser,
        updateAttribute: UpdateAttribute
    ): AuthenticationResult = withContext(defaultDispatcher) {
        val currentUser = firebaseAuth.currentUser!!
        runCatching {
            when (updateAttribute) {
                is UpdateAttribute.Email -> {
                    currentUser.changeEmail(
                        updateAttribute.newEmail,
                        updateAttribute.password
                    )
                }
                is UpdateAttribute.Password -> {
                    currentUser.changePassword(
                        updateAttribute.newPassword,
                        updateAttribute.oldPassword
                    )
                }
                is UpdateAttribute.Name -> {
                    currentUser.changeUserName(updateAttribute.newName)
                }
                is UpdateAttribute.ProfilePhotoUri -> {
                    currentUser.changePhotoUri(updateAttribute.newPhotoUri)
                }
            }
            // getting the updated user object
            val updatedUserObject = firebaseAuth.currentUser!!.toExamerUser()
            // assigning the updated user object to the livedata
            _currentUser.postValue(updatedUserObject)
            // passing the updated user object as an argument to success
            AuthenticationResult.Success(updatedUserObject)
        }.getOrElse {
            if (it is CancellationException) throw it
            AuthenticationResult.Failure(AuthenticationResult.FailureType.NetworkFailure)
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
