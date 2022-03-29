package com.example.examer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

//TODO Add docs
@Entity
data class UserAnswersEntity(
    val testDetailsId: String, // this indicates which test
    val associatedWorkBookId: String,// this indicates which workbook in the test
    val multiChoiceQuestionId: String,// this indicates which mcq in the workbook
    val indexOfCorrectOption: Int,
    val indexOfChosenOption: Int,
    @PrimaryKey(autoGenerate = true) val id: Int = -1 // will be autogen by Room
)