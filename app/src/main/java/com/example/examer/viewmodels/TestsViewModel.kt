package com.example.examer.viewmodels

import androidx.annotation.MainThread
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.WorkBook
import kotlinx.coroutines.launch

/**
 * An enum indicating the state of a screen using [TestsViewModel].
 */
enum class TestsViewModelUiState { LOADING, SUCCESSFULLY_LOADED }

/**
 *  An enum indicating which type of [TestDetails] list
 *  a particular instance of [ExamerTestsViewModel] will
 *  be working with.
 */
enum class TestDetailsListType { SCHEDULED_TESTS, PREVIOUS_TESTS }

/**
 * An interface that contains all the properties and methods required
 * for a concrete implementation of [TestsViewModel]
 */
interface TestsViewModel {
    /**
     * A state property that is holds a list of [TestDetails].
     */
    val testDetailsList: State<List<TestDetails>>

    /**
     * A state property that holds that hold the current [TestsViewModelUiState].
     */
    val testsViewModelUiState: State<TestsViewModelUiState>

    /**
     * Used to refresh [testDetailsList].
     */
    fun refreshTestDetailsList()

    /**
     * Used to mark the specified test as missed.
     *
     * @param testDetails the test to mark as missed.
     */
    fun markTestAsMissed(testDetails: TestDetails)

    /**
     * Used to fetch a list of workbooks for the specified test.
     *
     * @param testDetails the test for which the list of workbooks
     * is to be fetched.
     * @param onSuccess a callback that will be executed on the main
     * thread, if the fetching operation was successful.
     * @param onFailure an optional callback that will be executed
     * on the main thread, if the fetching operation was un-successful.
     */
    fun fetchWorkBookListForTestDetails(
        testDetails: TestDetails,
        @MainThread onSuccess: (List<WorkBook>) -> Unit,
        @MainThread onFailure: ((Throwable) -> Unit)? = null
    )
}

/**
 * A viewModel that can be used to in conjunction with a screen used
 * to display a list of [TestDetails] items.
 * @param authenticationService the authentication service to be used.
 * @param repository a reference to the repository that the viewModel
 * will use to fetch the data.
 * @param testDetailsListType the type of [TestDetailsListType] that
 * the viewModel would be responsible for. For example, if the viewModel
 * is passed [TestDetailsListType.SCHEDULED_TESTS] all operations
 * within the viewModel will be performed with respect to scheduled
 * tests.
 */
class ExamerTestsViewModel(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
    private val testDetailsListType: TestDetailsListType
) : ViewModel(), TestsViewModel {
    private val _testDetailsList: MutableState<List<TestDetails>> = mutableStateOf(listOf())
    private var _testsViewModelUiState: MutableState<TestsViewModelUiState> =
        mutableStateOf(TestsViewModelUiState.LOADING)
    override val testDetailsList: State<List<TestDetails>> = _testDetailsList
    override val testsViewModelUiState: State<TestsViewModelUiState> = _testsViewModelUiState

    init {
        fetchAndAssignTestDetailsList()
    }

    override fun refreshTestDetailsList() {
        fetchAndAssignTestDetailsList()
    }

    override fun fetchWorkBookListForTestDetails(
        testDetails: TestDetails,
        onSuccess: (List<WorkBook>) -> Unit,
        onFailure: ((Throwable) -> Unit)?
    ) {
        val currentUser = authenticationService.currentUser.value ?: return
        viewModelScope.launch {
            repository.fetchWorkBookList(currentUser, testDetails)
                .fold(onSuccess = onSuccess, onFailure = { onFailure?.invoke(it) })
        }
    }

    override fun markTestAsMissed(testDetails: TestDetails) {
        val currentUser = authenticationService.currentUser.value ?: return
        viewModelScope.launch { repository.markTestAsMissed(currentUser, testDetails.id) }
    }

    /**
     * Used to fetch the list of [TestDetails] associated with the
     * [AuthenticationService.currentUser] and setting it to the
     * [_testDetailsList] backing property. It also manages
     * the [testsViewModelUiState].
     */
    private fun fetchAndAssignTestDetailsList() {
        viewModelScope.launch {
            _testsViewModelUiState.value = TestsViewModelUiState.LOADING
            _testDetailsList.value = authenticationService
                .currentUser.value?.let { fetchTestDetailsList(it) } ?: emptyList()
            _testsViewModelUiState.value = TestsViewModelUiState.SUCCESSFULLY_LOADED
        }
    }

    /**
     * A suspend function that is used to get a list of [TestDetails]
     * based on the [testDetailsListType].
     */
    private suspend fun fetchTestDetailsList(user: ExamerUser) = when (testDetailsListType) {
        TestDetailsListType.SCHEDULED_TESTS -> repository.fetchActiveTestListForUser(user)
        TestDetailsListType.PREVIOUS_TESTS -> repository.fetchPreviousTestListForUser(user)
    }
}