package com.example.examer.data.domain

import com.example.examer.data.dto.UserAnswersDTO
import kotlinx.serialization.Serializable

/**
 * A value class that models the index of the option that is
 * chosen by the user for a [MultiChoiceQuestion].
 */
@Serializable
@JvmInline
value class IndexOfChosenOption(val value: Int) {
    override fun toString(): String = value.toString()
}

/**
 * A class that models the answers that are chosen by the user for a
 * particular [WorkBook].
 * @param associatedWorkBookId the id of the associated [WorkBook].
 * @param answers a map that contains  the [IndexOfChosenOption] for
 * each [MultiChoiceQuestion].
 * @param marksObtainedForWorkBook indicates the total marks obtained
 * for the [WorkBook] with the [associatedWorkBookId].
 */
@Serializable
data class UserAnswers(
    val associatedWorkBookId: String,
    val answers: Map<MultiChoiceQuestion, IndexOfChosenOption>,
    val marksObtainedForWorkBook: Int
)

/**
 * A converter method that is used to convert an instance of
 * [UserAnswers] to an instance of [UserAnswersDTO].
 */
fun UserAnswers.toUserAnswersDTO() = UserAnswersDTO(
    associatedWorkBookId = associatedWorkBookId,
    answersDetailsMap = answers.keys.map {
        mapOf(
            "multiChoiceQuestionId" to it.id,
            "indexOfCorrectOption" to it.indexOfCorrectOption.toString(),
            "indexOfChosenOption" to answers[it].toString()
        )
    },
    marksObtainedForWorkBook = marksObtainedForWorkBook.toString()
)
