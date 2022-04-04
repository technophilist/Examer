package com.example.examer.ui.navigation

import com.example.examer.data.domain.MultiChoiceQuestion
import com.example.examer.data.domain.WorkBook
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

sealed class TakeTestScreenDestinations(val route: String) {
    object ListenToAudioScreen :
        TakeTestScreenDestinations("com.example.examer.ui.navigation.WorkBookScreen")

    object WorkBookScreen :
        TakeTestScreenDestinations("com.example.examer.ui.navigation.WorkBookScreen/{testDetailsId}/{workBookId}/{questionsList}/{isLastWorkBook}/") {
        const val WORKBOOK_ID_ARG = "workBookId"
        const val QUESTIONS_LIST_ARG = "questionsList"
        const val TEST_DETAILS_ID_ARG = "testDetailsId"
        const val IS_LAST_WORKBOOK_ARG = "isLastWorkBook"
        fun buildRoute(
            testDetailsId: String,
            workBookId: String,
            multiChoiceQuestionList: List<MultiChoiceQuestion>,
            isLastWorkBook: Boolean = false
        ): String {
            val jsonString = Json.encodeToString(multiChoiceQuestionList)
            return "com.example.examer.ui.navigation.WorkBookScreen/${testDetailsId}/$workBookId/$jsonString/${isLastWorkBook}/"
        }
    }
}