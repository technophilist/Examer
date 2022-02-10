package com.example.examer.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.data.domain.TestDetails
import kotlinx.coroutines.launch

/**
 * An enum indicating the state of the HomeScreen.
 */
enum class HomeScreenUiState { LOADING, SUCCESSFULLY_LOADED } // TODO remove

/**
 *  An enum indicating which type of [TestDetails] list
 *  a particular instance of [ExamerTestsViewModel] will
 *  be working with.
 */
enum class TestDetailsListType { SCHEDULED_TESTS, PREVIOUS_TESTS }

interface TestsViewModel {
    val testDetailsList: State<List<TestDetails>>
    val homeScreenUiState: State<HomeScreenUiState>
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
    private var _homeScreenUiState: MutableState<HomeScreenUiState> =
        mutableStateOf(HomeScreenUiState.LOADING)
    override val testDetailsList: State<List<TestDetails>> = _testDetailsList
    override val homeScreenUiState: State<HomeScreenUiState> = _homeScreenUiState

    init {
        fetchAndAssignTestDetailsList(testDetailsListType)
    }

    override fun refreshTestDetailsList() {
        fetchAndAssignTestDetailsList(testDetailsListType)
    }

    /**
     * Used to fetch the list of [TestDetails] associated with the
     * [AuthenticationService.currentUser] and setting it to the
     * [_testDetailsList] backing property. It also manages
     * the [homeScreenUiState].
     */
    private fun fetchAndAssignTestDetailsList(listType: TestDetailsListType) {
        viewModelScope.launch {
            _homeScreenUiState.value = HomeScreenUiState.LOADING
            _testDetailsList.value = when (listType) {
                TestDetailsListType.SCHEDULED_TESTS -> fetchScheduledTestListForCurrentUser()
                TestDetailsListType.PREVIOUS_TESTS -> fetchPreviousTestListForCurrentUser()
            } ?: emptyList()
            _homeScreenUiState.value = HomeScreenUiState.SUCCESSFULLY_LOADED
        }
    }

    /**
     * Used to fetch the list of [TestDetails] associated with the
     *[AuthenticationService.currentUser]. This function will return
     * null if the current user is null.
     */
    private suspend fun fetchScheduledTestListForCurrentUser(): List<TestDetails>? =
        authenticationService
            .currentUser?.let { repository.fetchTestListForUser(it) }

    private suspend fun fetchPreviousTestListForCurrentUser(): List<TestDetails>? {
        TODO()
    }

}