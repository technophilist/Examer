package com.example.examer.data.remote

import android.graphics.Bitmap
import android.net.Uri
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.Status
import com.example.examer.data.domain.TestDetails
import com.example.examer.di.DispatcherProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FirebaseRemoteDatabase(private val dispatcherProvider: DispatcherProvider) : RemoteDatabase {

    override suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails> =
        withContext(dispatcherProvider.io) {
            val scheduledTestsCollection = fetchCollection(getCollectionPathForScheduledTests(user))
            // if no collection exists for the user, which likely indicates
            // that the user is a newly registered user, an empty list will
            // be returned.
            scheduledTestsCollection.documents.map { it.toTestDetails() }
        }

    override suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails> =
        withContext(dispatcherProvider.io) {
            val previousTestsCollection = fetchCollection(getCollectionPathForPreviousTests(user))
            // if no collection exists for the user, which likely indicates
            // that the user is a newly registered user, an empty list will
            // be returned.
            previousTestsCollection.documents.map { it.toTestDetails() }
        }

    override suspend fun saveBitmap(
        bitmap: Bitmap,
        fileName: String
    ): Result<Uri> = withContext(dispatcherProvider.io) {
        try {
            val byteArrayOutputStream = ByteArrayOutputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                it
            }
            val data = byteArrayOutputStream.toByteArray()
            Firebase.storage
                .reference
                .child("$PROFILE_PICTURES_FOLDER_NAME/$fileName.jpg")
                .putBytes(data)
                .await()
            val uri = Firebase.storage.reference
                .child("$PROFILE_PICTURES_FOLDER_NAME/$fileName.jpg")
                .downloadUrl
                .await()
            Result.success(uri)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }

    private fun DocumentSnapshot.toTestDetails() = TestDetails(
        id = id,
        title = get("title").toString(),
        description = get("description").toString(),
        language = get("language").toString(),
        localDateTime = getLocalDateTimeForTimeStamp(get("timestamp").toString().toLong()),
        totalNumberOfQuestions = get("totalNumberOfQuestions").toString().toInt(),
        testDurationInMinutes = get("testDurationInMinutes").toString().toInt(),
        testStatus = Status.valueOf((get("testStatus").toString().uppercase()))
    )

    private fun getLocalDateTimeForTimeStamp(timestamp: Long): LocalDateTime =
        Instant
            .ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

    private suspend fun fetchCollection(collectionPath: String) = Firebase.firestore
        .collection(collectionPath)
        .get()
        .await()

    companion object {
        private const val PROFILE_PICTURES_FOLDER_NAME = "profile_pics"
        private fun getCollectionPathForScheduledTests(user: ExamerUser) =
            "users/${user.id}/scheduledTests"

        private fun getCollectionPathForPreviousTests(user: ExamerUser) =
            "users/${user.id}/previousTests"
    }

}