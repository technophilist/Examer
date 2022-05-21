package com.example.examer.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.example.examer.data.domain.*
import com.example.examer.data.dto.AudioFileDTO
import com.example.examer.data.dto.WorkBookDTO
import com.example.examer.data.dto.toMultiChoiceQuestion
import com.example.examer.data.remote.RemoteDatabase
import com.example.examer.delegates.UpdateProfileUriDelegate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

/**
 * An interface that contains the requisite methods for an instance
 * of [Repository].
 */
interface Repository {
    /**
     * Used to the save the profile picture (represented as a [Bitmap])
     * for the specified [user].
     */
    suspend fun saveProfilePictureForUser(user: ExamerUser, bitmap: Bitmap)

    /**
     * Used to fetch a list of active tests for the specified [user].
     * An active test is any instance of [TestDetails] with [TestDetails.testStatus]
     * set to [Status.OPEN] or [Status.SCHEDULED]. The returned list will contain
     * both, tests which are open and tests which are scheduled.
     */
    suspend fun fetchActiveTestListForUser(user: ExamerUser): List<TestDetails>

    /**
     * Used to fetch a list of previous tests for the specified [user].
     *
     * A previous test is any instance of [TestDetails] with
     * [TestDetails.testStatus] set to either [Status.MISSED] or
     * [Status.COMPLETED]. The returned list will contain both, tests
     * which are missed and tests which are completed.
     */
    suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails>

    /**
     * Used to fetch the [TestResult] of a single test with the
     * specified [testDetailsId] for a specific [user].
     */
    suspend fun fetchTestResults(user: ExamerUser, testDetailsId: String): TestResult

    /**
     * Used to fetch a list of [WorkBook]s associated with a specific
     * [TestDetails] object, for the specified [user]. An instance of
     * [Result] containing the list is returned.
     */
    suspend fun fetchWorkBookList(
        user: ExamerUser,
        testDetails: TestDetails
    ): Result<List<WorkBook>>

    /**
     * Used to save the [userAnswers] associated with a particular test,
     * with the specified [testDetailId], for the specified [user].
     */
    suspend fun saveUserAnswersForUser(
        user: ExamerUser,
        userAnswers: UserAnswers,
        testDetailId: String
    )

    /**
     * Used to mark a test with the associated [testDetailId] as complete.
     * @param user indicates the user who is taking the test.
     */
    suspend fun markTestAsCompleted(user: ExamerUser, testDetailId: String)

    /**
     * Used to mark a test with the associated [testDetailId] as missed.
     * @param user indicates the user who is taking the test.
     */
    suspend fun markTestAsMissed(user: ExamerUser, testDetailId: String)
}

/**
 * A concrete implementation of [Repository].
 */
class ExamerRepository(
    private val context: Context,
    private val remoteDatabase: RemoteDatabase,
    private val updateProfileUriDelegate: UpdateProfileUriDelegate
) : Repository {
    override suspend fun fetchActiveTestListForUser(user: ExamerUser): List<TestDetails> =
        remoteDatabase.fetchActiveTestListForUser(user)

    override suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails> =
        remoteDatabase.fetchPreviousTestListForUser(user)

    override suspend fun saveProfilePictureForUser(user: ExamerUser, bitmap: Bitmap) {
        val photoUri = remoteDatabase.saveBitmap(
            bitmap = bitmap,
            fileName = user.id
        ).getOrThrow()
        updateProfileUriDelegate.update(photoUri) // can throw exception
    }

    override suspend fun fetchWorkBookList(
        user: ExamerUser,
        testDetails: TestDetails
    ): Result<List<WorkBook>> = try {
        val result = remoteDatabase.fetchWorkBookList(user, testDetails)
            .getOrThrow()
            .map { workBookDto ->
                // save the audio file associated with each workbook to
                // internal storage
                val localAudioFileUri = saveAudioFileToInternalStorage(
                    url = workBookDto.audioFile.audioFileUrl,
                    fileName = "${testDetails.id}_workbook${workBookDto.id}.wav"
                )
                val examerAudioFile = workBookDto.audioFile.toExamerAudioFile(localAudioFileUri)
                workBookDto.toWorkBook(examerAudioFile)
            }
        Result.success(result)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override suspend fun saveUserAnswersForUser(
        user: ExamerUser,
        userAnswers: UserAnswers,
        testDetailId: String
    ) {
        // throws exception
        remoteDatabase.saveUserAnswers(user, userAnswers, testDetailId)
    }

    override suspend fun markTestAsCompleted(user: ExamerUser, testDetailId: String) {
        // throws exception
        remoteDatabase.markTestAsCompleted(user, testDetailId)
    }

    override suspend fun markTestAsMissed(user: ExamerUser, testDetailId: String) {
        // throws exception
        remoteDatabase.markTestAsMissed(user, testDetailId)
    }

    override suspend fun fetchTestResults(
        user: ExamerUser,
        testDetailsId: String
    ): TestResult = remoteDatabase.fetchResultsForTest(user, testDetailsId)

    /**
     * Used to convert an instance of [AudioFileDTO] to an instance
     * of [ExamerAudioFile].
     */
    private fun AudioFileDTO.toExamerAudioFile(localAudioFileUri: Uri) = ExamerAudioFile(
        localAudioFileUri = localAudioFileUri,
        numberOfRepeatsAllowedForAudioFile = numberOfRepeatsAllowedForAudioFile
    )

    /**
     * Used to convert an instance of [WorkBookDTO] to an instance
     * of [WorkBook].
     */
    private fun WorkBookDTO.toWorkBook(audioFile: ExamerAudioFile) = WorkBook(
        id = id,
        audioFile = audioFile,
        questions = questions.map { it.toMultiChoiceQuestion() }
    )

    /**
     * Used to save the an audio file stored in the specified [url]
     * to the specified [fileName].
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun saveAudioFileToInternalStorage(
        url: URL,
        fileName: String
    ): Uri = withContext(Dispatchers.IO) {
        runCatching {
            val audioFile = File(context.filesDir, fileName)
            val audioFileOutputStream = audioFile.outputStream()
            // open the url stream and copy the stream to the output stream
            url.openStream().use { it.copyTo(audioFileOutputStream) }
            // close the outputStream
            audioFileOutputStream.close()
            // return uri of audio file
            audioFile.toUri()
        }.getOrThrow()
    }
}
