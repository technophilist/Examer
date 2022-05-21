package com.example.examer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.*
import com.example.examer.data.domain.IndexOfChosenOption
import com.example.examer.data.domain.MultiChoiceQuestion
import com.example.examer.data.domain.UserAnswers
import com.example.examer.data.workers.SaveUserAnswersWorker
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface WorkBookViewModel {
    fun saveUserAnswersForTestId(
        questionsList: List<MultiChoiceQuestion>,
        answersMap: Map<MultiChoiceQuestion, IndexOfChosenOption>,
        testDetailsId: String,
        workBookId: String
    )
}

class ExamerWorkBookViewModel(
    application: Application
) : AndroidViewModel(application), WorkBookViewModel {
    private val workManager = WorkManager.getInstance(application)
    override fun saveUserAnswersForTestId(
        questionsList: List<MultiChoiceQuestion>,
        answersMap: Map<MultiChoiceQuestion, IndexOfChosenOption>,
        testDetailsId: String,
        workBookId: String
    ) {
        val userAnswers = UserAnswers(
            associatedWorkBookId = workBookId,
            answers = answersMap,
            marksObtainedForWorkBook = computeMarks(questionsList, answersMap)
        )
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
            .setInputData(inputData)
            .build()
        workManager.enqueue(workRequest)
    }

    private fun computeMarks(
        questionsList: List<MultiChoiceQuestion>,
        answersMap: Map<MultiChoiceQuestion, IndexOfChosenOption>
    ): Int = questionsList.fold(0) { acc, mcq ->
        acc + if (answersMap[mcq]!!.value == mcq.indexOfCorrectOption) mcq.mark else 0
    }
}