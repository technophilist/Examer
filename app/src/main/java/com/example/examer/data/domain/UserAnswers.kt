package com.example.examer.data.domain

import com.example.examer.data.dto.UserAnswersDTO

@JvmInline
value class IndexOfChosenOption(val index: Int) {
    override fun toString(): String = index.toString()
}

data class UserAnswers(
    val associatedWorkBookId: String,
    val answers: Map<MultiChoiceQuestion, IndexOfChosenOption>
)

fun UserAnswers.toUserAnswersDTO() = UserAnswersDTO(
    associatedWorkBookId = associatedWorkBookId,
    answersDetailsMap = answers.keys.map {
        mapOf(
            "id" to it.id,
            "indexOfCorrectOption" to it.indexOfCorrectOption.toString(),
            "indexOfChosenOption" to answers[it].toString()
        )
    }
)
