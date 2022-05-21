package com.example.examer.viewmodels

import android.media.MediaPlayer
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examer.utils.buildCountDownTimer
import com.example.examer.utils.toString
import java.util.concurrent.TimeUnit
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.WorkBook
import com.example.examer.ui.screens.listenToAudioScreen.PlaybackState
import com.example.examer.usecases.MarkTestAsCompletedUseCase
import kotlinx.coroutines.Job
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
    /**
     * An enum class that contains different UI stats associated  with
     * a concrete implementation of [TestSessionViewModel].
     */
    enum class UiState { IDLE, TEST_TIMED_OUT }

    /**
     * A state property that contains the current [UiState] of the
     * [TestSessionViewModel].
     */
    val uiState: State<UiState>

    // TODO - add docs
    val playbackState: State<PlaybackState>

    /**
     * A state property that contains the current [WorkBook].
     */
    val currentWorkBook: State<WorkBook>

    /**
     * A state property that contains the current workbook number.
     */
    val currentWorkBookNumber: State<Int>

    /**
     * A state property that contains a string indicating the number
     * of hours remaining for the test.
     */
    val hoursRemaining: State<String>

    /**
     * A state property that contains a string indicating the number
     * of minutes remaining for the test.
     */
    val minutesRemaining: State<String>

    /**
     * A state property that contains a string indicating the number
     * of seconds remaining for the test.
     */
    val secondsRemaining: State<String>

    /**
     * A state property that contains a string indicating the number
     * of repeats left for the **current** audio file .
     */
    val numberOfRepeatsLeftForAudioFile: State<Int>

    /**
     * A state property that indicates whether the **current** audio
     * is playing.
     */
    val isAudioFilePlaying: State<Boolean>

    /**
     * Used to play the audio file for the [currentWorkBook].
     */
    fun playAudioForCurrentWorkBook()

    /**
     * Used to update the [currentWorkBook] value to the next workbook.
     */
    fun moveToNextWorkBook()

    /**
     * Used to mark the current test as complete.
     */
    fun markCurrentTestAsComplete()

    /**
     * Used to stop the playback of the **currently** playing
     * audio file.
     */
    fun stopAudioPlayback()
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
    private val _playbackState = mutableStateOf(PlaybackState())
    override val playbackState = _playbackState as State<PlaybackState>
    private val _numberOfRepeatsLeftForAudioFile =
        mutableStateOf(_currentWorkBook.value.audioFile.numberOfRepeatsAllowedForAudioFile)
    override val numberOfRepeatsLeftForAudioFile = _numberOfRepeatsLeftForAudioFile as State<Int>
    private val _isAudioFilePlaying = mutableStateOf(false)
    private var setPlaybackProgressCoroutineJob: Job? = null
    override val isAudioFilePlaying = _isAudioFilePlaying as State<Boolean>

    private val countDownTimer = buildCountDownTimer(
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

    /**
     * Used to start a coroutine, that will set the [_playbackProgress]
     * based on the state of the [player].
     */
    private fun setProgressBasedOnMediaPlayerState(player: MediaPlayer) {
        setPlaybackProgressCoroutineJob = viewModelScope.launch {
            while (player.isPlaying) {
                ensureActive()
                _playbackState.value.currentProgress =
                    player.currentPosition / player.duration.toFloat()
                delay(1_000)
            }
            // when the media player has finished playing, the loop
            // will be exited. This means that the progress value
            // will never reach 1.0f since the body of the loop would
            // not be executed for one last time - when the player has
            // finished playing the audio file. In order to accommodate
            // for that, set the value to 1.0f outside the loop.
            _playbackState.value.currentProgress = 1.0f
            // set the playbackProgress back to zero after 1 second to
            // indicate to the user that the playback has completed.
            delay(1_000)
            _playbackState.value.currentProgress = 0.0f
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

    override fun stopAudioPlayback() {
        if (!mediaPlayer.isPlaying) return
        mediaPlayer.stop()
        mediaPlayer.reset()
        _isAudioFilePlaying.value = false
    }

    /**
     * Utility function used to create a time string based on the
     * [hour],[minute] and [second] parameters
     */
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

    /**
     * Used to convert the specified [minute] to a [Long] representing
     * the [minute] in milliseconds.
     */
    private fun convertMinuteToMillis(minute: Int): Long = minute * 60_000L
}