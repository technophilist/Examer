package com.example.examer.data.remote

import android.graphics.Bitmap
import android.net.Uri
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails

interface RemoteDatabase {
    suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun saveBitmap(bitmap: Bitmap, fileName: String): Uri
}