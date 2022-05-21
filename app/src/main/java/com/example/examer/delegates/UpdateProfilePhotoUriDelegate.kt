package com.example.examer.delegates

import android.net.Uri
import com.example.examer.auth.AuthenticationResult
import com.example.examer.auth.AuthenticationService
import com.example.examer.utils.PasswordManager

/**
 * An interface that specifies the methods that are a requisite
 * for a concrete implementation of [UpdateProfileUriDelegate].
 */
interface UpdateProfileUriDelegate {
    suspend fun update(uri: Uri)
}

/**
 * A concrete implementation of [UpdateProfileUriDelegate].
 * @param authenticationService an instance of [AuthenticationService]
 * that will be used to get the [AuthenticationService.currentUser].
 */
class ExamerUpdateProfileUriDelegate(
    private val authenticationService: AuthenticationService
) : UpdateProfileUriDelegate {
    override suspend fun update(uri: Uri) {
        authenticationService.currentUser.value?.let {
            val result = authenticationService.updateAttributeForUser(
                user = it,
                updateAttribute = AuthenticationService.UpdateAttribute.ProfilePhotoUri(uri)
            )
            if (result is AuthenticationResult.Failure) {
                throw Exception(result.failureType.toString())
            }
        }
    }
}
