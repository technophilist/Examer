package com.example.examer.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails
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
    val testDetailsList: State<List<TestDetails>>
    val testsViewModelUiState: State<TestsViewModelUiState>
    fun refreshTestDetailsList()
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
        TestDetailsListType.SCHEDULED_TESTS -> repository.fetchScheduledTestListForUser(user)
        TestDetailsListType.PREVIOUS_TESTS -> repository.fetchPreviousTestListForUser(user)
    }
}