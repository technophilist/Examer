package com.example.examer.data.remote

import android.graphics.Bitmap
import android.net.Uri
import com.example.examer.data.domain.*
import com.example.examer.data.dto.AudioFileDTO
import com.example.examer.data.dto.MultiChoiceQuestionListDTO
import com.example.examer.data.dto.WorkBookDTO
import com.example.examer.di.DispatcherProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.net.URL
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FirebaseRemoteDatabase(private val dispatcherProvider: DispatcherProvider) : RemoteDatabase {

    override suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails> =
        withContext(dispatcherProvider.io) {
            val scheduledTestsCollection = fetchCollection(
                collectionPath = getCollectionPathForTests(user),
                runOnCollectionReference = { whereEqualTo("testStatus", "scheduled") }
            )
            // if no collection exists for the user, which likely indicates
            // that the user is a newly registered user, an empty list will
            // be returned.
            scheduledTestsCollection.documents.map { it.toTestDetails() }
        }

    override suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails> =
        withContext(dispatcherProvider.io) {
            val previousTestsCollection = fetchCollection(
                collectionPath = getCollectionPathForTests(user),
                runOnCollectionReference = { whereIn("testStatus", listOf("completed", "missed")) }
            )
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

    override suspend fun fetchWorkBookList(
        user: ExamerUser,
        testDetails: TestDetails
    ): Result<List<WorkBookDTO>> = withContext(dispatcherProvider.io) {
        try {
            val workbooksCollectionPath = getCollectionPathForWorkBooks(user, testDetails)
            val workbooksCollection = fetchCollection(workbooksCollectionPath)
                .documents
                .map { it.toWorkBookDTO() }
            Result.success(workbooksCollection)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }

    override suspend fun saveUserAnswers(
        user: ExamerUser,
        userAnswers: UserAnswers,
        testDetailsId: String
    ) {
        withContext(dispatcherProvider.io) {
            Firebase.firestore
                .collection(getCollectionPathForUserAnswers(user, testDetailsId))
                .document()
                .set(userAnswers.toUserAnswersDTO())
                .await() // throws exception
        }
    }

    override suspend fun markTestAsCompleted(user: ExamerUser, testDetailsId: String) {
        withContext(dispatcherProvider.io) {
            Firebase.firestore
                .document("${getCollectionPathForTests(user)}/$testDetailsId")
                .update("testStatus", Status.COMPLETED.toString().lowercase())
                .await() // throws exception
        }
    }

    private fun DocumentSnapshot.toWorkBookDTO(): WorkBookDTO {
        val examerAudioFile = AudioFileDTO(
            audioFileUrl = URL(get("audioFileDownloadUrl").toString()),
            numberOfRepeatsAllowedForAudioFile = get("numberOfRepeatsAllowedForAudioFile").toString()
                .toInt()
        )
        val questionsJsonString = get("questionsJsonList").toString()
        val multiChoiceQuestionDtoList =
            Json.decodeFromString<MultiChoiceQuestionListDTO>(questionsJsonString)
        val multiChoiceQuestionList = multiChoiceQuestionDtoList.questions
        return WorkBookDTO(
            id = id,
            audioFile = examerAudioFile,
            questions = multiChoiceQuestionList
        )
    }

    private fun DocumentSnapshot.toTestDetails() = TestDetails(
        id = id,
        title = get("title").toString(),
        description = get("description").toString(),
        language = get("language").toString(),
        localDateTime = getLocalDateTimeForTimeStamp(get("timestamp").toString().toLong()),
        totalNumberOfWorkBooks = get("totalNumberOfWorkBooks").toString().toInt(),
        testDurationInMinutes = get("testDurationInMinutes").toString().toInt(),
        testStatus = Status.valueOf((get("testStatus").toString().uppercase()))
    )

    private fun getLocalDateTimeForTimeStamp(timestamp: Long): LocalDateTime =
        Instant
            .ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

    //TODO add doc
    private suspend fun fetchCollection(
        collectionPath: String,
        runOnCollectionReference: (CollectionReference.() -> Query)? = null
    ): QuerySnapshot = Firebase
        .firestore
        .collection(collectionPath)
        .run {
            // CollectionReference is a subclass of Query.
            // if the block is not null, run the block
            // and return the Query object returned by the block.
            // if block is null, return the collection reference.
            runOnCollectionReference?.invoke(this) ?: this
        }
        .get()
        .await()

    companion object {
        private const val PROFILE_PICTURES_FOLDER_NAME = "profile_pics"
        private fun getCollectionPathForTests(user: ExamerUser) = "users/${user.id}/tests"
        private fun getCollectionPathForWorkBooks(user: ExamerUser, testDetails: TestDetails) =
            "${getCollectionPathForTests(user)}/${testDetails.id}/workbooks"

        private fun getCollectionPathForUserAnswers(user: ExamerUser, testDetailsId: String) =
            "${getCollectionPathForTests(user)}/${testDetailsId}/answersForEachWorkBook"
    }

}