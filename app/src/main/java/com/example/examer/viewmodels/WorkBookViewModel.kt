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

/**
 * An interface that contains the requisite methods for a concrete
 * implementation of [WorkBookViewModel].
 */
interface WorkBookViewModel {
    /**
     * Used to save the answers selected by the user for the specified
     * workbook of a specific test.
     * @param questionsList a list of all the questions in the workbook.
     * @param answersMap a map tha contains the question and the answer
     * selected by the user for each question.
     * @param testDetailsId the id of the associated test.
     * @param workBookId the id of the workbook for which the
     * answers are to be saved.
     */
    fun saveUserAnswersForTestId(
        questionsList: List<MultiChoiceQuestion>,
        answersMap: Map<MultiChoiceQuestion, IndexOfChosenOption>,
        testDetailsId: String,
        workBookId: String
    )
}

/**
 * A concrete implementation of [WorkBookViewModel].
 */
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

    /**
     * A utility method that is used to compute the total marks scored
     * for a specific workbook.
     * @param questionsList a list of all questions in the workbook.
     * @param answersMap a map that contains all the questions and the
     * correct answer for each respective question in the workbook.
     */
    private fun computeMarks(
        questionsList: List<MultiChoiceQuestion>,
        answersMap: Map<MultiChoiceQuestion, IndexOfChosenOption>
    ): Int = questionsList.fold(0) { acc, mcq ->
        acc + if (answersMap[mcq]!!.value == mcq.indexOfCorrectOption) mcq.mark else 0
    }
}