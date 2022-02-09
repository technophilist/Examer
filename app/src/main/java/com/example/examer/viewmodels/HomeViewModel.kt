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

interface HomeViewModel {
    val testDetailsList: State<List<TestDetails>>
}

// TODO change HomeViewModel to ScheduledTestViewModel
class ExamerHomeViewModel(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
) : ViewModel(), HomeViewModel {
    private val _testDetailsList: MutableState<List<TestDetails>> = mutableStateOf(listOf())
    override val testDetailsList: State<List<TestDetails>> = _testDetailsList

    init {
        viewModelScope.launch {
            _testDetailsList.value = authenticationService
                .currentUser?.let { repository.fetchTestListForUser(it) } ?: emptyList()
        }
    }
}