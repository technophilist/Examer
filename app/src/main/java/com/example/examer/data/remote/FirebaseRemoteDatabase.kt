package com.example.examer.data.remote

import android.graphics.Bitmap
import android.net.Uri
import com.example.examer.data.domain.*
import com.example.examer.data.dto.*
import com.example.examer.di.DispatcherProvider
import com.google.firebase.firestore.*
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
                collectionPath = getCollectionPathForTests(user.id),
                runOnCollectionReference = { whereEqualTo("testStatus", "scheduled") }
            )
            // if no collection exists for the user, which likely indicates
            // that the user is a newly registered user, an empty list will
            // be returned.
            scheduledTestsCollection.documents.map { it.toTestDetailsDTO().toTestDetails() }
        }

    override suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails> =
        withContext(dispatcherProvider.io) {
            val previousTestsCollection = fetchCollection(
                collectionPath = getCollectionPathForTests(user.id),
                runOnCollectionReference = { whereIn("testStatus", listOf("completed", "missed")) }
            )
            // if no collection exists for the user, which likely indicates
            // that the user is a newly registered user, an empty list will
            // be returned.
            previousTestsCollection.documents.map { it.toTestDetailsDTO().toTestDetails() }
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
            val workbooksCollectionPath = getCollectionPathForWorkBooks(user.id, testDetails.id)
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
                .collection(getCollectionPathForUserAnswers(user.id, testDetailsId))
                .document()
                .set(userAnswers.toUserAnswersDTO())
                .await() // throws exception
        }
    }

    override suspend fun markTestAsCompleted(user: ExamerUser, testDetailsId: String) {
        withContext(dispatcherProvider.io) {
            Firebase.firestore
                .document("${getCollectionPathForTests(user.id)}/$testDetailsId")
                .update("testStatus", Status.COMPLETED.toString().lowercase())
                .await() // throws exception
        }
    }

    override suspend fun markTestAsMissed(user: ExamerUser, testDetailsId: String) {
        withContext(dispatcherProvider.io) {
            Firebase.firestore
                .document("${getCollectionPathForTests(user.id)}/$testDetailsId")
                .update("testStatus", Status.MISSED.toString().lowercase())
                .await() // throws exception
        }
    }

    override suspend fun fetchResultsForTest(
        user: ExamerUser,
        testDetailsId: String
    ): TestResult = withContext(dispatcherProvider.io) {
        val marksObtained = fetchCollection(getCollectionPathForUserAnswers(user.id, testDetailsId))
            .documents
            .map { it.toUserAnswersDTO() }
            .fold(0) { acc, userAnswersDTO -> acc + userAnswersDTO.marksObtainedForWorkBook.toInt() }
        val maximumMarks = Firebase.firestore
            .document("${getCollectionPathForTests(user.id)}/$testDetailsId")
            .get()
            .await()
            .getString("maximumMarks")!!
            .toInt()// TODO create/use test details dto object

        TestResult(
            testDetailsId = testDetailsId,
            marksObtained = marksObtained,
            maximumMarks = maximumMarks
        )
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

    private fun DocumentSnapshot.toTestDetailsDTO() = TestDetailsDTO(
        id = id,
        title = getString("title")!!,
        description = getString("description")!!,
        language = getString("language")!!,
        timeStamp = getString("timestamp")!!,
        totalNumberOfWorkBooks = getString("totalNumberOfWorkBooks")!!,
        testDurationInMinutes = getString("testDurationInMinutes")!!,
        testStatus = getString("testStatus")!!,
        maximumMarks = getString("maximumMarks")!!
    )


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
        private fun getCollectionPathForWorkBooks(
            userId: String,
            testDetailsId: String
        ) = "${getCollectionPathForTests(userId)}/${testDetailsId}/workbooks"

        private fun getCollectionPathForUserAnswers(
            userId: String,
            testDetailsId: String
        ) = "${getCollectionPathForTests(userId)}/${testDetailsId}/answersForEachWorkBook"

        private fun getCollectionPathForTests(userId: String) = "users/${userId}/tests"
    }

}