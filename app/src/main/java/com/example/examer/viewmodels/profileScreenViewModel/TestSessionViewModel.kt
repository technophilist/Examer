package com.example.examer.viewmodels.profileScreenViewModel

import androidx.annotation.FloatRange
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.examer.auth.AuthenticationService
import com.example.examer.data.Repository
import com.example.examer.utils.createCountDownTimer
import com.example.examer.utils.toString
import java.util.concurrent.TimeUnit
import com.example.examer.data.domain.TestDetails

/**
 * An interface that contains the methods and properties that are
 * required for a concrete implementation of [TestSessionViewModel].
 *
 * This type of viewModel is used to encapsulate logic that is required
 * to manage a single test session. In other words, it contains the logic
 * that is used to manage a single test ([TestDetails]).
 */
interface TestSessionViewModel {
    val currentWorkBookNumber: State<Int>
    val hoursRemaining: State<String>
    val minutesRemaining: State<String>
    val secondsRemaining: State<String>
    val numberOfRepeatsLeftForAudioFile: State<Int>
    val playbackProgress: State<Float>
    fun playAudio()
//    fun getWorkBook()
}

class ExamerTestSessionViewModel(
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
    private val testDetails: TestDetails
) : ViewModel(), TestSessionViewModel {
    // state for workbook
    private val _currentWorkBookNumber = mutableStateOf(1)
    override val currentWorkBookNumber = _currentWorkBookNumber as State<Int>

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
    private val maximumNumberOfRepeatsAllowed: Int = 3 // TODO hardcoded
    private val _numberOfRepeatsLeftForAudioFile = mutableStateOf(maximumNumberOfRepeatsAllowed)
    override val numberOfRepeatsLeftForAudioFile = _numberOfRepeatsLeftForAudioFile as State<Int>

    // playback progress states
    @FloatRange(from = 0.0, to = 1.0)
    private val _playbackProgress = mutableStateOf(0.0f)
    override val playbackProgress = _playbackProgress as State<Float>

    init {
        countDownTimer.start()
    }

    override fun playAudio() {
        if (_numberOfRepeatsLeftForAudioFile.value - 1 < 0) {
            stopPlayingAudio()
            return
        }
        _numberOfRepeatsLeftForAudioFile.value = numberOfRepeatsLeftForAudioFile.value - 1
        // start playingg....
    }

    private fun stopPlayingAudio() {
//        TODO()
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