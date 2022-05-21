package com.example.examer.data.domain

/**
 * A class that models the results of single test.
 *
 * @param testDetailsId the id of the [TestDetails] object that this
 * instance is associated with. In other words, it indicates
 * the test that the marks are associated to.
 * @param marksObtained indicates the total marks obtained by the user
 * for the test.
 * @param maximumMarks indicates the maximum marks that can be obtained.
 */
data class TestResult(
    val testDetailsId: String,
    val marksObtained: Int,
    val maximumMarks: Int
)
