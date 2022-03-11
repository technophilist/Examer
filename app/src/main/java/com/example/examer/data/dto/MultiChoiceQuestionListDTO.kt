package com.example.examer.data.dto

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class MultiChoiceQuestionListDTO(val questions: List<MultiChoiceQuestionDTO>)