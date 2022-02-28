package com.example.examer.data

import android.graphics.Bitmap
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.remote.RemoteDatabase
import com.example.examer.usecases.UpdateProfilePhotoUriUseCase
import kotlinx.coroutines.CancellationException

interface Repository {
    suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun saveProfilePictureForUser(user: ExamerUser, bitmap: Bitmap)
}

class ExamerRepository(
    private val remoteDatabase: RemoteDatabase,
    private val updateProfilePhotoUriUseCase: UpdateProfilePhotoUriUseCase
) : Repository {
    override suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails> =
        remoteDatabase.fetchScheduledTestListForUser(user)

    override suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails> =
        remoteDatabase.fetchPreviousTestListForUser(user)

    override suspend fun saveProfilePictureForUser(user: ExamerUser, bitmap: Bitmap) {
        try {
            val photoUri = remoteDatabase.saveBitmap(
                bitmap = bitmap,
                fileName = user.id
            ).getOrThrow()
            updateProfilePhotoUriUseCase.update(photoUri) // can throw exception
        } catch (exception: Exception) {
            throw exception
        }
    }
}