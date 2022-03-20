package com.example.examer.data.dto

import kotlinx.serialization.Serializable

/**
 * A DTO value class that wraps a list of [MultiChoiceQuestionListDTO].
 */
@Serializable
@JvmInline
value class MultiChoiceQuestionListDTO(val questions: List<MultiChoiceQuestionDTO>)