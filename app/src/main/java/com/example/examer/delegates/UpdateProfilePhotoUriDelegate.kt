package com.example.examer.delegates

import android.net.Uri
import com.example.examer.auth.AuthenticationResult
import com.example.examer.auth.AuthenticationService
import com.example.examer.utils.PasswordManager

interface UpdateProfileUriDelegate {
    suspend fun update(uri: Uri)
}

class ExamerUpdateProfileUriDelegate(
    private val authenticationService: AuthenticationService,
    private val passwordManager: PasswordManager
) : UpdateProfileUriDelegate {
    override suspend fun update(uri: Uri) {
        authenticationService.currentUser.value?.let {
            val result = authenticationService.updateAttributeForUser(
                it,
                AuthenticationService.UpdateAttributeType.PROFILE_PHOTO_URI,
                uri.toString(),
                passwordManager.getPasswordForUser(it) // TODO Possible un-necessary arg for profile photo
            )
            if (result is AuthenticationResult.Failure) {
                throw Exception(result.failureType.toString())
            }
        }
    }
}
