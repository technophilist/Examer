package com.example.examer.data.domain

import kotlinx.serialization.Serializable

/**
 * Class that models a single question in a [WorkBook].
 *
 * @param id the id of the Multi-Choice question.
 * @param question a string that contains the question.
 * @param options an array of strings representing the options for the
 * multi-choice question.
 * @param indexOfCorrectOption integer indicating the index of correct
 * option.
 * @param mark represents the maximum weightage (mark) for this
 * question.
 */
@Serializable
data class MultiChoiceQuestion(
    val id: String,
    val question: String,
    val options: Array<String>,
    val indexOfCorrectOption: Int,
    val mark: Int,
) : java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MultiChoiceQuestion

        if (id != other.id) return false
        if (question != other.question) return false
        if (!options.contentEquals(other.options)) return false
        if (indexOfCorrectOption != other.indexOfCorrectOption) return false
        if (mark != other.mark) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + question.hashCode()
        result = 31 * result + options.contentHashCode()
        result = 31 * result + indexOfCorrectOption
        result = 31 * result + mark
        return result
    }
}
