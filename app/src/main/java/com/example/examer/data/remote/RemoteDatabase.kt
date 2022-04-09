package com.example.examer.data.remote

import android.graphics.Bitmap
import android.net.Uri
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.UserAnswers
import com.example.examer.data.domain.WorkBook
import com.example.examer.data.dto.WorkBookDTO

interface RemoteDatabase {
    suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun fetchWorkBookList(
        user: ExamerUser,
        testDetails: TestDetails
    ): Result<List<WorkBookDTO>>

    suspend fun saveBitmap(bitmap: Bitmap, fileName: String): Result<Uri>
    suspend fun saveUserAnswers(
        user: ExamerUser,
        userAnswers: UserAnswers,
        testDetailsId: String
    )

    suspend fun markTestAsCompleted(user: ExamerUser, testDetailsId: String)
    suspend fun markTestAsMissed(user: ExamerUser, testDetailsId: String)
    suspend fun saveNotificationToken(user: ExamerUser, notificationToken: String)
}