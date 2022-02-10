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
enum class HomeScreenUiState { LOADING, SUCCESSFULLY_LOADED }
interface TestsViewModel {
    val testDetailsList: State<List<TestDetails>>
    val homeScreenUiState: State<HomeScreenUiState>
    fun refreshTestDetailsList()
}

class ExamerTestsViewModel(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
) : ViewModel(), TestsViewModel {
    private val _testDetailsList: MutableState<List<TestDetails>> = mutableStateOf(listOf())
    private var _homeScreenUiState: MutableState<HomeScreenUiState> =
        mutableStateOf(HomeScreenUiState.LOADING)
    override val testDetailsList: State<List<TestDetails>> = _testDetailsList
    override val homeScreenUiState: State<HomeScreenUiState> = _homeScreenUiState

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
     * the [homeScreenUiState].
     */
    private fun fetchAndAssignTestDetailsList() {
        viewModelScope.launch {
            _homeScreenUiState.value = HomeScreenUiState.LOADING
            _testDetailsList.value = fetchTestListForCurrentUser() ?: emptyList()
            _homeScreenUiState.value = HomeScreenUiState.SUCCESSFULLY_LOADED
        }
    }

    /**
     * Used to fetch the list of [TestDetails] associated with the
     *[AuthenticationService.currentUser]. This function will return
     * null if the current user is null.
     */
    private suspend fun fetchTestListForCurrentUser(): List<TestDetails>? = authenticationService
        .currentUser?.let { repository.fetchTestListForUser(it) }

}