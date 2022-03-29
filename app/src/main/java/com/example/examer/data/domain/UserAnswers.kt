package com.example.examer.data.domain

import com.example.examer.data.dto.UserAnswersDTO
import com.example.examer.data.local.UserAnswersEntity

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
            "multiChoiceQuestionId" to it.id,
            "indexOfCorrectOption" to it.indexOfCorrectOption.toString(),
            "indexOfChosenOption" to answers[it].toString()
        )
    }
)

//TODO add doc explaining why a single UserAnswersObject is converted to a list
fun UserAnswers.toUserAnswersEntityList(testDetailsId: String): List<UserAnswersEntity> =
    answers.keys.map { mcq ->
        UserAnswersEntity(
            testDetailsId = testDetailsId,
            associatedWorkBookId = associatedWorkBookId,
            multiChoiceQuestionId = mcq.id,
            indexOfCorrectOption = mcq.indexOfCorrectOption,
            indexOfChosenOption = answers[mcq]!!.index
        )
    }
