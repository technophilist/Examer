package com.example.examer.auth

import android.net.Uri
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await

/**
 * This extension method is used for creating a [FirebaseUser] with the
 * provided [name],[email],[password] and [profilePhotoUri].
 *
 * Firebase doesn't provide a default method to create a user along with a
 * display name and profile photo.In order to perform such a task we need to
 * chain two methods - [FirebaseAuth.createUserWithEmailAndPassword] and
 * [FirebaseAuth.updateCurrentUser].
 * @throws  FirebaseAuthWeakPasswordException  thrown if the password is not strong enough
 * @throws FirebaseAuthInvalidCredentialsException thrown if the email address is malformed
 * @throws FirebaseAuthUserCollisionException thrown if there already exists an account with the given email address
 * @throws FirebaseAuthInvalidUserException thrown if the current user's account has been disabled, deleted, or its credentials are no longer valid.
 */
suspend fun FirebaseAuth.createUser(
    name: String,
    email: String,
    password: String,
    profilePhotoUri: Uri?
): FirebaseUser = runCatching {
    createUserWithEmailAndPassword(email, password).await()
    //if user is created successfully, set the display name and profile picture
    val userProfileChangeRequest = UserProfileChangeRequest.Builder()
        .setDisplayName(name)
        .setPhotoUri(profilePhotoUri)
        .build()
    currentUser!!.updateProfile(userProfileChangeRequest).await()
    currentUser!!
}.getOrThrow()


suspend fun FirebaseUser.changeEmail(newEmail: String, password: String) {
    runCatchingRecentLoginException(password) { updateEmail(newEmail).await() }
}

suspend fun FirebaseUser.changePassword(newPassword: String, oldPassword: String) {
    runCatchingRecentLoginException(oldPassword) { updatePassword(newPassword).await() }
}

/**
 * A utility function that executes the given [updateBlock] and
 * tries to re-run the block if the [updateBlock] throws
 * a [FirebaseAuthRecentLoginRequiredException]. If the block
 * throws any other exception, it will be re-thrown.
 *
 * This is mainly meant to be used with [FirebaseUser]'s update
 * functions such as [FirebaseUser.updateEmail] and
 * [FirebaseUser.updatePassword] which throw an instance of
 * [FirebaseAuthRecentLoginRequiredException] if the user has
 * not logged-in recently. If such an exception is thrown, the
 * update block will be called again after re-authenticating
 * the user using the [FirebaseUser.reauthenticate] method.
 *
 * @param password the password to use in the [FirebaseUser.reauthenticate]
 * method.
 */
suspend fun FirebaseUser.runCatchingRecentLoginException(
    password: String,
    updateBlock: suspend () -> Unit
) {
    runCatching { updateBlock() }
        .onFailure {
            if (it is FirebaseAuthRecentLoginRequiredException) {
                // try to change the email after re-authenticating
                // NOTE: re-authenticate can also throw exceptions
                // It is left to the caller to handle the exception
                reauthenticate(EmailAuthProvider.getCredential(email!!, password)).await()
                updateBlock()
            } else {
                throw it
            }
        }
}

/**
 * Utility method to update the display name of an instance of
 * [FirebaseUser] with the [newName]. If any exception occurs,
 * it throws the exception.
 */
suspend fun FirebaseUser.changeUserName(newName: String) {
    val userProfileChangeRequest = UserProfileChangeRequest.Builder()
        .setDisplayName(newName)
        .build()
    updateProfile(userProfileChangeRequest).await()
}

