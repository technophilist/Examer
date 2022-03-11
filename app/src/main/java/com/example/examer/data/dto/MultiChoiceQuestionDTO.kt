package com.example.examer.data.dto

import com.example.examer.data.domain.MultiChoiceQuestion
import kotlinx.serialization.Serializable

@Serializable
data class MultiChoiceQuestionDTO(
    val questionNumber: Int,
    val question: String,
    val options: Array<String>,
    val indexOfCorrectOption: Int,
    val markForQuestion: Int,
)

fun MultiChoiceQuestionDTO.toMultiChoiceQuestion() = MultiChoiceQuestion(
    id = "id", // TODO
    question = question,
    options = options,
    indexOfCorrectOption = indexOfCorrectOption,
    mark = markForQuestion
)