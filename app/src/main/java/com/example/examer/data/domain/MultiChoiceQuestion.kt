package com.example.examer.data.domain

/**
 * Class that models a single question in a [WorkBook]
 */
data class MultiChoiceQuestion(
    val id: String,
    val question: String,
    val options: Array<String>,
    val indexOfCorrectOption: Int,
    val mark:Int,
) {
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
