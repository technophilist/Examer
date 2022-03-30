package com.example.examer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.data.domain.UserAnswers
import kotlinx.coroutines.launch

interface WorkBookViewModel {
    fun saveUserAnswersForTestId(userAnswers: UserAnswers, testDetailsId: String)
}

class ExamerWorkBookViewModel(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application), WorkBookViewModel {
    private val workManager = WorkManager.getInstance(application)
    override fun saveUserAnswersForTestId(userAnswers: UserAnswers, testDetailsId: String) {
        viewModelScope.launch {
            repository.saveUserAnswersForUser(
                user = authenticationService.currentUser.value!!,
                userAnswers = userAnswers,
                testDetailId = testDetailsId
            )
        }
    }
}