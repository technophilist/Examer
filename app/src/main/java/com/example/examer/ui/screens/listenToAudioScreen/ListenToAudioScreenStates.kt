package com.example.examer.ui.screens.listenToAudioScreen

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.saveable.rememberSaveable

data class AudioPlaybackState(
    val isEnabled: State<Boolean>,
    /*@FloatRange(from = 0.0, to = 1.0)*/ val progress: State<Float>,
    val numberOfRepeatsLeft: State<Int>
)

