package com.example.examer.data.domain

data class UserAnswers(
    val associatedWorkBookId: String,
    val answers: Map<MultiChoiceQuestion, Int>
)