package com.example.examer.data.remote

import androidx.compose.ui.text.toUpperCase
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.Status
import com.example.examer.data.domain.TestDetails
import com.example.examer.di.DispatcherProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FirebaseRemoteDatabase(private val dispatcherProvider: DispatcherProvider) : RemoteDatabase {

    override suspend fun fetchTestListForUser(user: ExamerUser): List<TestDetails> =
        withContext(dispatcherProvider.io) {
            val scheduledTestsCollection = Firebase.firestore
                .collection("users/${user.id}/scheduledTests") // TODO change id
                .get()
                .await()
            if (scheduledTestsCollection.isEmpty) {
                // collections cannot be empty. An empty collection indicates that
                // the collection does not exist.
                throw IllegalArgumentException("Unable to fetch document.Invalid user.")
            }
            scheduledTestsCollection.documents.map { it.toTestDetails() }
        }

    private fun DocumentSnapshot.toTestDetails() = TestDetails(
        id = id,
        title = get("title").toString(),
        description = get("description").toString(),
        language = get("language").toString(),
        localDateTime = getLocalDateTimeForTimeStamp(get("timestamp").toString().toLong()),
        totalNumberOfQuestions = get("totalNumberOfQuestions").toString().toInt(),
        minutesPerQuestion = get("minutesPerQuestion").toString().toInt(),
        testStatus = Status.valueOf((get("testStatus").toString().uppercase()))
    )

    private fun getLocalDateTimeForTimeStamp(timestamp: Long): LocalDateTime =
        Instant
            .ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()


}