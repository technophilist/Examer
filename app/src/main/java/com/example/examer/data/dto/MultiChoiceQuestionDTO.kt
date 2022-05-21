package com.example.examer.data.dto

import com.example.examer.data.domain.MultiChoiceQuestion
import kotlinx.serialization.Serializable

/**
 * A DTO object for [MultiChoiceQuestion].
 */
@Serializable
data class MultiChoiceQuestionDTO(
    val questionNumber: Int,
    val question: String,
    val options: Array<String>,
    val indexOfCorrectOption: Int,
    val markForQuestion: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MultiChoiceQuestionDTO

        if (questionNumber != other.questionNumber) return false
        if (question != other.question) return false
        if (!options.contentEquals(other.options)) return false
        if (indexOfCorrectOption != other.indexOfCorrectOption) return false
        if (markForQuestion != other.markForQuestion) return false

        return true
    }

    override fun hashCode(): Int {
        var result = questionNumber
        result = 31 * result + question.hashCode()
        result = 31 * result + options.contentHashCode()
        result = 31 * result + indexOfCorrectOption
        result = 31 * result + markForQuestion
        return result
    }
}

/**
 * Extension function used to convert an instance of [MultiChoiceQuestionDTO]
 * to an instance of [MultiChoiceQuestion].
 */
fun MultiChoiceQuestionDTO.toMultiChoiceQuestion() = MultiChoiceQuestion(
    id = questionNumber.toString(),
    question = question,
    options = options,
    indexOfCorrectOption = indexOfCorrectOption,
    mark = markForQuestion
)