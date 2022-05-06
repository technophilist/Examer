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

interface Repository {
    suspend fun saveProfilePictureForUser(user: ExamerUser, bitmap: Bitmap)
    suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun fetchTestResults(user: ExamerUser, testDetailsId: String): TestResult
    suspend fun fetchWorkBookList(
        user: ExamerUser,
        testDetails: TestDetails
    ): Result<List<WorkBook>>

    suspend fun saveUserAnswersForUser(
        user: ExamerUser,
        userAnswers: UserAnswers,
        testDetailId: String
    )

    suspend fun markTestAsCompleted(user: ExamerUser, testDetailId: String)
    suspend fun markTestAsMissed(user: ExamerUser, testDetailId: String)
}

class ExamerRepository(
    private val context: Context,
    private val remoteDatabase: RemoteDatabase,
    private val updateProfilePhotoUriUseCase: UpdateProfileUriDelegate
) : Repository {
    override suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails> =
        remoteDatabase.fetchScheduledTestListForUser(user)

    override suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails> =
        remoteDatabase.fetchPreviousTestListForUser(user)

    override suspend fun saveProfilePictureForUser(user: ExamerUser, bitmap: Bitmap) {
        // TODO Change to result class instead of re-throwing
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
        // todo exception handling
        remoteDatabase.saveUserAnswers(user, userAnswers, testDetailId)
    }

    override suspend fun markTestAsCompleted(user: ExamerUser, testDetailId: String) {
        // todo exception handling
        remoteDatabase.markTestAsCompleted(user, testDetailId)
    }

    override suspend fun markTestAsMissed(user: ExamerUser, testDetailId: String) {
        // todo exception handling
        remoteDatabase.markTestAsMissed(user, testDetailId)
    }

    override suspend fun fetchTestResults(
        user: ExamerUser,
        testDetailsId: String
    ): TestResult = remoteDatabase.fetchResultsForTest(user, testDetailsId)

    private fun AudioFileDTO.toExamerAudioFile(localAudioFileUri: Uri) = ExamerAudioFile(
        localAudioFileUri = localAudioFileUri,
        numberOfRepeatsAllowedForAudioFile = numberOfRepeatsAllowedForAudioFile
    )

    private fun WorkBookDTO.toWorkBook(audioFile: ExamerAudioFile) = WorkBook(
        id = id,
        audioFile = audioFile,
        questions = questions.map { it.toMultiChoiceQuestion() }
    )

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
