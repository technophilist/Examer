package com.example.examer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.*
import com.example.examer.data.domain.UserAnswers
import com.example.examer.data.workers.SaveUserAnswersWorker
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface WorkBookViewModel {
    fun saveUserAnswersForTestId(userAnswers: UserAnswers, testDetailsId: String)
}

class ExamerWorkBookViewModel(
    application: Application
) : AndroidViewModel(application), WorkBookViewModel {
    private val workManager = WorkManager.getInstance(application)
    override fun saveUserAnswersForTestId(userAnswers: UserAnswers, testDetailsId: String) {
        val workerConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val inputData = Data.Builder().run {
            val jsonFormat = Json { allowStructuredMapKeys = true }
            val userDetailsJsonString = jsonFormat.encodeToString(userAnswers)
            putString(SaveUserAnswersWorker.KEY_USER_ANSWERS_JSON_STRING_ARG, userDetailsJsonString)
            putString(SaveUserAnswersWorker.KEY_TEST_DETAILS_ID_ARG, testDetailsId)
            build()
        }
        val workRequest = OneTimeWorkRequest.Builder(SaveUserAnswersWorker::class.java)
            .setConstraints(workerConstraints)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(inputData)
            .build()
        workManager.enqueue(workRequest)
    }
}