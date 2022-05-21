package com.example.examer.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.examer.data.domain.UserAnswers
import com.example.examer.di.ExamerApplication
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * A [CoroutineWorker] that is used to save the answers of a user, for a
 * particular test.
 */
class SaveUserAnswersWorker(
    private val appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result = try {
        // deserialize user answers object
        val userAnswersJsonString = inputData.getString(KEY_USER_ANSWERS_JSON_STRING_ARG)!!
        val format = Json { allowStructuredMapKeys = true }
        val userAnswers = format.decodeFromString<UserAnswers>(userAnswersJsonString)
        // get testDetailsId
        val testDetailsId = inputData.getString(KEY_TEST_DETAILS_ID_ARG)!!
        // use the repository to save the UserAnswers object
        val appContainer = ((appContext) as ExamerApplication).appContainer
        val currentlyLoggedInUser = appContainer.authenticationService.currentUser.value!!
        val repository = appContainer.repository
        repository.saveUserAnswersForUser(currentlyLoggedInUser, userAnswers, testDetailsId)
        Result.success()
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure()
    }

    companion object {
        const val KEY_USER_ANSWERS_JSON_STRING_ARG =
            "com.example.examer.data.workers.SaveUserAnswersWorker.KEY_USER_ANSWERS_ARG"
        const val KEY_TEST_DETAILS_ID_ARG =
            "com.example.examer.data.workers.SaveUserAnswersWorker.KEY_TEST_DETAILS_ID_ARG"
    }
}