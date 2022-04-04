package com.example.examer.viewmodels

import android.media.MediaPlayer
import androidx.annotation.FloatRange
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examer.utils.createCountDownTimer
import com.example.examer.utils.toString
import java.util.concurrent.TimeUnit
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.WorkBook
import com.example.examer.usecases.ExamerMarkTestAsCompletedUseCase
import com.example.examer.usecases.MarkTestAsCompletedUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

/**
 * An interface that contains the methods and properties that are
 * required for a concrete implementation of [TestSessionViewModel].
 *
 * This type of viewModel is used to encapsulate logic that is required
 * to manage a single test session. In other words, it contains the logic
 * that is used to manage a single test ([TestDetails]).
 */
interface TestSessionViewModel {
    enum class UiState { IDLE, TEST_TIMED_OUT }

    val uiState: State<UiState>
    val currentWorkBook: State<WorkBook>
    val currentWorkBookNumber: State<Int>
    val hoursRemaining: State<String>
    val minutesRemaining: State<String>
    val secondsRemaining: State<String>
    val numberOfRepeatsLeftForAudioFile: State<Int>
    val playbackProgress: State<Float>
    val isAudioFilePlaying: State<Boolean>
    fun playAudioForCurrentWorkBook()
    fun moveToNextWorkBook()
    fun markCurrentTestAsComplete()
}

class ExamerTestSessionViewModel(
    private val mediaPlayer: MediaPlayer,
    private val testDetails: TestDetails,
    private val workBookList: List<WorkBook>,
    private val markTestAsCompletedUseCase: MarkTestAsCompletedUseCase
) : ViewModel(), TestSessionViewModel {
    private val _uiState = mutableStateOf(TestSessionViewModel.UiState.IDLE)
    override val uiState = _uiState as State<TestSessionViewModel.UiState>

    // variables for workBook
    private val _currentWorkBookIndex = mutableStateOf(0)
    override val currentWorkBookNumber = derivedStateOf { _currentWorkBookIndex.value + 1 }
    private val _currentWorkBook = mutableStateOf(workBookList.first())
    override val currentWorkBook = _currentWorkBook as State<WorkBook>

    // states for timer
    private val timeRemainingForTest = mutableStateOf(createTimeString(0, 0, 0))
    private val splitTextList = derivedStateOf { timeRemainingForTest.value.split(":") }
    override val hoursRemaining = derivedStateOf { splitTextList.value[0] }
    override val minutesRemaining = derivedStateOf { splitTextList.value[1] }
    override val secondsRemaining = derivedStateOf { splitTextList.value[2] }

    // Audio Playback
    private val _numberOfRepeatsLeftForAudioFile =
        mutableStateOf(_currentWorkBook.value.audioFile.numberOfRepeatsAllowedForAudioFile)
    override val numberOfRepeatsLeftForAudioFile = _numberOfRepeatsLeftForAudioFile as State<Int>
    private val _isAudioFilePlaying = mutableStateOf(false)
    private var setPlaybackProgressCoroutineJob: Job? = null
    override val isAudioFilePlaying = _isAudioFilePlaying as State<Boolean>

    private val countDownTimer = createCountDownTimer(
        millisInFuture = convertMinuteToMillis(testDetails.testDurationInMinutes),
        onTimerTick = { millis ->
            val hours = (TimeUnit.MILLISECONDS.toHours(millis) % 60).toInt()
            val minutes = (TimeUnit.MILLISECONDS.toMinutes(millis) % 60).toInt()
            val seconds = (TimeUnit.MILLISECONDS.toSeconds(millis) % 60).toInt()
            timeRemainingForTest.value = createTimeString(hours, minutes, seconds)
        },
        onTimerFinished = {
            markCurrentTestAsComplete()
            // stop updating the playback progress
            setPlaybackProgressCoroutineJob?.cancel()
            // stop playing media
            mediaPlayer.stop()
            // release the resources of the media player
            mediaPlayer.release()
            // set the appropriate ui state
            _uiState.value = TestSessionViewModel.UiState.TEST_TIMED_OUT
        }
    )

    // playback progress states
    /*@FloatRange(from = 0.0, to = 1.0)*/
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
            reset()
            setDataSource(_currentWorkBook.value.audioFile.localAudioFileUri.toString())
            prepare()
            start()
            _isAudioFilePlaying.value = true
            setProgressBasedOnMediaPlayerState(this)
            setOnCompletionListener { _isAudioFilePlaying.value = false }
        }
    }

    private fun setProgressBasedOnMediaPlayerState(player: MediaPlayer) {
        setPlaybackProgressCoroutineJob = viewModelScope.launch {
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

    override fun markCurrentTestAsComplete() {
        viewModelScope.launch { markTestAsCompletedUseCase.invoke(testDetails.id) }
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

    private fun convertMinuteToMillis(minute: Int): Long = minute * 60_000L
}