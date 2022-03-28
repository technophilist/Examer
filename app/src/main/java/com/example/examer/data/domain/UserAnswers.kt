package com.example.examer.data.domain

@JvmInline
value class IndexOfChosenOption(val index: Int)

data class UserAnswers(
    val associatedWorkBookId: String,
    val answers: Map<MultiChoiceQuestion, IndexOfChosenOption>
)