package com.example.examer.ui.screens.listenToAudioScreen

import androidx.compose.runtime.*

@Deprecated("Use PlaybackState class.", replaceWith = ReplaceWith("PlaybackState()"))
data class AudioPlaybackState(
    val isEnabled: State<Boolean>,
    /*@FloatRange(from = 0.0, to = 1.0)*/ val progress: State<Float>,
    val numberOfRepeatsLeft: State<Int>
)

class PlaybackState(
    isPlaybackEnabled: Boolean = true,
    /*@FloatRange(from = 0.0, to = 1.0)*/
    startProgress: Float = 0.0f
) {
    var isEnabled by mutableStateOf(isPlaybackEnabled)

    /*@FloatRange(from = 0.0, to = 1.0)*/
    var currentProgress by mutableStateOf(startProgress)
}
