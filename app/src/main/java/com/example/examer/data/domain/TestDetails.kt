package com.example.examer.data.domain

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

import java.time.format.DateTimeFormatter

/**
 * An enum class representing the status of the test.
 */
enum class Status { OPEN, SCHEDULED, MISSED, COMPLETED }

/**
 * A data class that models a test.
 *
 * @param title the title of the test.
 * @param description the description  of the test.
 * @param language indicates the language that the test
 * focuses on.
 * @param localDateTime an instance of [LocalDateTime] that indicates
 * the time and date of the test. The [getLocalDateTimeForMillis]
 * static method can be used to generate the instance for a given
 * timestamp in milliseconds.
 * @param totalNumberOfQuestions indicates the total number of
 * questions in this test.
 * @param minutesPerQuestion indicates the number of minutes allotted
 * for each question.
 */
data class TestDetails(
    val title: String,
    val description: String,
    val language: String,
    val localDateTime: LocalDateTime,
    val totalNumberOfQuestions: Int,
    val minutesPerQuestion: Int,
    val testStatus: Status
) {
    companion object {
        /**
         * Used to get an instance of [LocalDateTime] for the provided
         * [epochMillis] in milliseconds.
         */
        fun getLocalDateTimeForMillis(epochMillis: Long): LocalDateTime =
            Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
    }
}

/**
 * Used to get a pair consisting of strings representing date and time.
 * The first value in the pair is the date string and the second
 * value is the time.
 *
 * @param is24hourFormat used to specify whether the format of the time
 * string.
 */
fun TestDetails.getDateStringAndTimeString(is24hourFormat: Boolean = false): Pair<String, String> {
    val format = DateTimeFormatter.ofPattern(if (is24hourFormat) "hh:mm" else "h:mm a")
    val dateString = localDateTime.toLocalDate().toString()
    val timeString = localDateTime
        .toLocalTime()
        .format(format)
    return Pair(dateString, timeString)
}
