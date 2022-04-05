package com.example.examer.data.dto

import com.example.examer.data.domain.UserAnswers

/**
 * A DTO object for [UserAnswers].
 * @param associatedWorkBookId the id of the workbook associated
 * with the [UserAnswers] object.
 * @param answersDetailsMap a map with custom objects as keys are not
 * supported. Only strings are supported. Therefore, it is not
 * possible to use Map<MultiChoiceQuestion, IndexOfChosenOption>
 * for answers. To accommodate for that, a list of maps are used to
 * store the details of the each answer. A map in the list consists
 * of the details of a particular mcq question.
 */
data class UserAnswersDTO(
    val associatedWorkBookId: String,
    val answersDetailsMap: List<Map<String, String>>,
    val marksObtainedForWorkBook: Int
)