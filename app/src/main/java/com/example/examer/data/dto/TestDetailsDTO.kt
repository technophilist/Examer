package com.example.examer.data.dto

import com.example.examer.data.domain.TestDetails

/**
 * A DTO object equivalent to [TestDetails].
 */
data class TestDetailsDTO(
    val id: String,
    val title: String,
    val description: String,
    val language: String,
    val timeStamp: String,
    val totalNumberOfWorkBooks: String,
    val testDurationInMinutes: String,
    val testStatus: String,
    val maximumMarks: String
)
