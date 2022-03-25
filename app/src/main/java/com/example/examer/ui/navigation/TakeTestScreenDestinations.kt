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
        TakeTestScreenDestinations("com.example.examer.ui.navigation.WorkBookScreen/{questionsList}/") {
        const val QUESTIONS_LIST_ARG = "questionsList"
        fun buildRoute(multiChoiceQuestionList: List<MultiChoiceQuestion>): String {
            val jsonString = Json.encodeToString(multiChoiceQuestionList)
            return "com.example.examer.ui.navigation.WorkBookScreen/$jsonString/"
        }
    }
}