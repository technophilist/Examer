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
    var testDetailsList: State<List<TestDetails>>
}

class ExamerHomeViewModel(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
) : ViewModel(), HomeViewModel {
    private val _testDetailsList: MutableState<List<TestDetails>> = mutableStateOf(listOf())
    override var testDetailsList: State<List<TestDetails>> = _testDetailsList

    init {
        viewModelScope.launch {
            authenticationService.currentUser?.let { repository.fetchTestListForUser(it) }
        }
    }
}