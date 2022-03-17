package com.example.examer.viewmodels

import android.media.MediaPlayer
import androidx.annotation.FloatRange
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.data.domain.ExamerUser
import com.example.examer.utils.createCountDownTimer
import com.example.examer.utils.toString
import java.util.concurrent.TimeUnit
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.WorkBook
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

/**
 * An interface that contains the methods and properties that are
 * required for a concrete implementation of [TestSessionViewModel].
 *
 * This type of viewModel is used to encapsulate logic that is required
 * to manage a single test session. In other words, it contains the logic
 * that is used to manage a single test ([TestDetails]).
 */
interface TestSessionViewModel {
    enum class UiState { IDLE, LOADING, WORKBOOK_LIST_FETCH_ERROR }

    val uiState: State<UiState>
    val currentWorkBook: State<WorkBook?>
    val currentWorkBookNumber: State<Int>
    val hoursRemaining: State<String>
    val minutesRemaining: State<String>
    val secondsRemaining: State<String>
    val numberOfRepeatsLeftForAudioFile: State<Int>
    val playbackProgress: State<Float>
    fun playAudioForCurrentWorkBook()
    fun moveToNextWorkBook()
}

class ExamerTestSessionViewModel(
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
    private val mediaPlayer: MediaPlayer,
    private val testDetails: TestDetails,
    private val workBookList: List<WorkBook>
) : ViewModel(), TestSessionViewModel {
    private val _uiState = mutableStateOf(TestSessionViewModel.UiState.IDLE)
    override val uiState = _uiState as State<TestSessionViewModel.UiState>

    // variables for workBook
    private val _currentWorkBookIndex = mutableStateOf(0)
    override val currentWorkBookNumber = derivedStateOf { _currentWorkBookIndex.value + 1 }
    private val _currentWorkBook = mutableStateOf(workBookList.first())
    override val currentWorkBook by derivedStateOf { _currentWorkBook }

    // states for timer
    private val timeRemainingForTest = mutableStateOf(createTimeString(0, 0, 0))
    private val splitTextList = derivedStateOf { timeRemainingForTest.value.split(":") }
    override val hoursRemaining = derivedStateOf { splitTextList.value[0] }
    override val minutesRemaining = derivedStateOf { splitTextList.value[1] }
    override val secondsRemaining = derivedStateOf { splitTextList.value[2] }

    private val countDownTimer = createCountDownTimer(
        millisInFuture = testDetails.minutesPerQuestion.toLong(), // TODO remove 'minutes per quest'in test details class
        onTimerTick = { millis ->
            val hours = (TimeUnit.MILLISECONDS.toHours(millis) % 60).toInt()
            val minutes = (TimeUnit.MILLISECONDS.toMinutes(millis) % 60).toInt()
            val seconds = (TimeUnit.MILLISECONDS.toSeconds(millis) % 60).toInt()
            timeRemainingForTest.value = createTimeString(hours, minutes, seconds)
        }
    )

    // Audio Playback
    private val _numberOfRepeatsLeftForAudioFile =
        mutableStateOf(_currentWorkBook.value.audioFile.numberOfRepeatsAllowedForAudioFile)
    override val numberOfRepeatsLeftForAudioFile = _numberOfRepeatsLeftForAudioFile as State<Int>

    // playback progress states
    @FloatRange(from = 0.0, to = 1.0)
    private val _playbackProgress = mutableStateOf(0.0f)
    override val playbackProgress = _playbackProgress as State<Float>

    init {
        countDownTimer.start()
    }

    override fun playAudioForCurrentWorkBook() {
        // return if there are not repeats left or the media player is already playing
        if (_numberOfRepeatsLeftForAudioFile.value - 1 < 0 || mediaPlayer.isPlaying) return
        // decrement the value of umberOfRepeatsLeftForAudioFile variable by 1
        _numberOfRepeatsLeftForAudioFile.value -= 1
        // use media player to start audio playback
        mediaPlayer.run {
            setDataSource(_currentWorkBook.value.audioFile.localAudioFileUri.toString())
            prepare()
            start()
            setProgressBasedOnMediaPlayerState(this)
        }
    }

    private fun setProgressBasedOnMediaPlayerState(player: MediaPlayer) {
        viewModelScope.launch {
            while (player.isPlaying) {
                ensureActive()
                _playbackProgress.value = player.currentPosition / player.duration.toFloat()
                delay(1_000)
            }
            _playbackProgress.value = 1.0f
        }
    }

    override fun moveToNextWorkBook() {
        // if incrementing the index value makes the index value >=
        // the size of the workbook, return
        if (_currentWorkBookIndex.value + 1 >= workBookList.size) return
        // increment the index value
        _currentWorkBookIndex.value++
        // assign the workbook at the incremented index to the current workbook variable
        _currentWorkBook.value = workBookList[_currentWorkBookIndex.value]
        // set the number of repeats allowed for the audio file associated
        // with the new workbook
        _numberOfRepeatsLeftForAudioFile.value =
            _currentWorkBook.value.audioFile.numberOfRepeatsAllowedForAudioFile
    }

    private fun createTimeString(
        hour: Int,
        minute: Int,
        second: Int
    ): String {
        val hourString = hour.toString(appendZeroIfSingleDigit = true)
        val minuteString = minute.toString(appendZeroIfSingleDigit = true)
        val secondString = second.toString(appendZeroIfSingleDigit = true)
        return "$hourString:$minuteString:$secondString"
    }
}