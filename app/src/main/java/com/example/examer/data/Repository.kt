package com.example.examer.data

import android.graphics.Bitmap
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.remote.RemoteDatabase

interface Repository {
    suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun saveProfilePictureForUser(user: ExamerUser, bitmap: Bitmap)
}

class ExamerRepository(private val remoteDatabase: RemoteDatabase) : Repository {
    override suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails> =
        remoteDatabase.fetchScheduledTestListForUser(user)

    override suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails> =
        remoteDatabase.fetchPreviousTestListForUser(user)

    override suspend fun saveProfilePictureForUser(user: ExamerUser, bitmap: Bitmap) {
        remoteDatabase.saveBitmap(bitmap = bitmap, fileName = user.id)
    }
}