package com.example.examer.data.dto

import com.example.examer.data.domain.Status
import com.example.examer.data.domain.TestDetails
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

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

fun TestDetailsDTO.toTestDetails() = TestDetails(
    id = id,
    title = title,
    description = description,
    language = language,
    localDateTime = getLocalDateTimeForTimeStamp(timeStamp.toLong()),
    totalNumberOfWorkBooks = totalNumberOfWorkBooks.toInt(),
    testDurationInMinutes = testDurationInMinutes.toInt(),
    testStatus = Status.valueOf(testStatus.uppercase())
)

private fun getLocalDateTimeForTimeStamp(timestamp: Long): LocalDateTime =
    Instant
        .ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()