package com.example.examer.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.TestResult
import kotlinx.coroutines.launch

/**
 * An enum class used to model the different UI states associated with
 * a concrete implementation of [PreviousTestsViewModel].
 */
enum class PreviousTestsViewModelUiState { LOADING, SUCCESSFULLY_LOADED }

/**
 * An interface that specifies the requisite properties and methods
 * required for a concrete implementation of [PreviousTestsViewModel].
 */
interface PreviousTestsViewModel {
    /**
     * A state property that contains the current [PreviousTestsViewModelUiState].
     */
    val uiState: State<PreviousTestsViewModelUiState>

    /**
     * A state property that contains the a map that stores [TestDetails]
     * object and the associated [TestResult] object.
     */
    val testResultsMap: State<Map<TestDetails, TestResult>>

    /**
     * Used to refresh the previous test list.
     */
    fun refreshPreviousTestsList()
}

class ExamerPreviousTestsViewModel(
    private val authenticationService: AuthenticationService,
    private val repository: Repository
) : ViewModel(), PreviousTestsViewModel {

    private val _uiState = mutableStateOf(PreviousTestsViewModelUiState.LOADING)
    private val _testResultsMap = mutableStateOf<Map<TestDetails, TestResult>>(emptyMap())
    override val uiState = _uiState as State<PreviousTestsViewModelUiState>
    override val testResultsMap = _testResultsMap as State<Map<TestDetails, TestResult>>

    init {
        fetchAndAssignPreviousTestsList()
    }

    override fun refreshPreviousTestsList() {
        viewModelScope.launch { fetchAndAssignPreviousTestsList() }
    }

    /**
     * Used to fetching a list of previous tests and assigning them to
     * [_testResultsMap]. An empty map will be assigned if the
     * [AuthenticationService.currentUser] is null.
     */
    private fun fetchAndAssignPreviousTestsList() {
        viewModelScope.launch {
            _uiState.value = PreviousTestsViewModelUiState.LOADING
            _testResultsMap.value = authenticationService.currentUser.value?.let { user ->
                val previousTests = repository.fetchPreviousTestListForUser(user)
                previousTests.associateWith { repository.fetchTestResults(user, it.id) }
            } ?: emptyMap()
            _uiState.value = PreviousTestsViewModelUiState.SUCCESSFULLY_LOADED
        }
    }
}
