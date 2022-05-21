package com.example.examer.data.remote

import android.graphics.Bitmap
import android.net.Uri
import com.example.examer.data.domain.*
import com.example.examer.data.dto.WorkBookDTO

/**
 * An interface that contains the requisite methods for a remote
 * database.
 */
interface RemoteDatabase {
    /**
     * Used to fetch a list of active tests for the specified [user].
     * An active test is any instance of [TestDetails] with [TestDetails.testStatus]
     * set to [Status.OPEN] or [Status.SCHEDULED]. The returned list will contain
     * both, tests which are open and tests which are scheduled.
     */
    suspend fun fetchActiveTestListForUser(user: ExamerUser): List<TestDetails>

    /**
     * Used to fetch a list of previous tests for the specified [user].
     *
     * A previous test is any instance of [TestDetails] with
     * [TestDetails.testStatus] set to either [Status.MISSED] or
     * [Status.COMPLETED]. The returned list will contain both, tests
     * which are missed and tests which are completed.
     */
    suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails>

    /**
     * Used to fetch the [TestResult] of a single test with the
     * specified [testDetailsId] for a specific [user].
     */
    suspend fun fetchResultsForTest(user: ExamerUser, testDetailsId: String): TestResult

    /**
     * Used to fetch a list of [WorkBook]s associated with a specific
     * [TestDetails] object, for the specified [user]. An instance of
     * [Result] containing the list is returned.
     */
    suspend fun fetchWorkBookList(
        user: ExamerUser,
        testDetails: TestDetails
    ): Result<List<WorkBookDTO>>

    /**
     * Used to save an instance of [bitmap] with the specified [fileName].
     * It returns an instance of [Result] containing the associated [Uri].
     */
    suspend fun saveBitmap(bitmap: Bitmap, fileName: String): Result<Uri>

    /**
     * Used to save the [userAnswers] associated with a particular test,
     * with the specified [testDetailsId], for the specified [user].
     */
    suspend fun saveUserAnswers(
        user: ExamerUser,
        userAnswers: UserAnswers,
        testDetailsId: String
    )

    /**
     * Used to mark a test with the associated [testDetailsId] as complete.
     * @param user indicates the user who is taking the test.
     */
    suspend fun markTestAsCompleted(user: ExamerUser, testDetailsId: String)

    /**
     * Used to mark a test with the associated [testDetailsId] as missed.
     * @param user indicates the user who is taking the test.
     */
    suspend fun markTestAsMissed(user: ExamerUser, testDetailsId: String)
}