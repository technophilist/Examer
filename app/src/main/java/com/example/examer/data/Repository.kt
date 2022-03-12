package com.example.examer.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.example.examer.data.domain.ExamerAudioFile
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.WorkBook
import com.example.examer.data.remote.RemoteDatabase
import com.example.examer.usecases.UpdateProfilePhotoUriUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

interface Repository {
    suspend fun saveProfilePictureForUser(user: ExamerUser, bitmap: Bitmap)
    suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun fetchWorkBookList(
        user: ExamerUser,
        testDetails: TestDetails
    ): Result<List<WorkBook>>
}

class ExamerRepository(
    private val context: Context,
    private val remoteDatabase: RemoteDatabase,
    private val updateProfilePhotoUriUseCase: UpdateProfilePhotoUriUseCase
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
            .map { workBook ->
                // save the audio file associated with each workbook to
                // internal storage
                val audioFileUri = saveAudioFileToInternalStorage(
                    url = URL(workBook.audioFile.audioFileUri.toString()),
                    fileName = "${testDetails.id}_workbook${workBook.id}.wav"
                )
                // add the uri of the locally stored audio file to a new
                // instance of ExamerAudioFile class
                val audioFile = ExamerAudioFile(
                    audioFileUri = audioFileUri,
                    numberOfRepeatsAllowedForAudioFile = workBook.audioFile.numberOfRepeatsAllowedForAudioFile
                )
                // swap out the existing audio file class with
                // the new audio file class containing the uri
                // of the locally saved audio file.
                workBook.copy(audioFile = audioFile)
            }
        Result.success(result)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }


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
