package com.example.examer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.data.domain.UserAnswers
import com.example.examer.data.workers.SaveUserAnswersWorker
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface WorkBookViewModel {
    fun saveUserAnswersForTestId(userAnswers: UserAnswers, testDetailsId: String)
}

class ExamerWorkBookViewModel(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application), WorkBookViewModel {
    private val workManager = WorkManager.getInstance(application)
    override fun saveUserAnswersForTestId(userAnswers: UserAnswers, testDetailsId: String) {
        val workerConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val inputData = Data.Builder().run {
            val userDetailsJsonString = Json.encodeToString(userAnswers)
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