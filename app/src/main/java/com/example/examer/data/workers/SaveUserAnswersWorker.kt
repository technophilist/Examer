package com.example.examer.data.workers

import android.app.Application
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.examer.data.domain.UserAnswers
import com.example.examer.di.ExamerApplication
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SaveUserAnswersWorker(
    private val application: Application,
    workerParameters: WorkerParameters
) : CoroutineWorker(application, workerParameters) {
    override suspend fun doWork(): Result = try {
        // deserialize user answers object
        val userAnswersJsonString = inputData.getString(KEY_USER_ANSWERS_JSON_STRING_ARG)!!
        val userAnswers = Json.decodeFromString<UserAnswers>(userAnswersJsonString)
        // get testDetailsId
        val testDetailsId = inputData.getString(KEY_TEST_DETAILS_ID_ARG)!!
        // use the repository to save the UserAnswers object
        val appContainer = ((application) as ExamerApplication).appContainer
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