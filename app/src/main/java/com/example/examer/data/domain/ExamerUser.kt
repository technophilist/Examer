package com.example.examer.data.domain

import android.net.Uri

/**
 * A domain model class that represents a user with a [id],[name],
 * [email],[phoneNumber] and a [photoUrl] that will be used as the
 * profile picture of the user.
 */
data class ExamerUser(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val photoUrl: Uri? = null
)