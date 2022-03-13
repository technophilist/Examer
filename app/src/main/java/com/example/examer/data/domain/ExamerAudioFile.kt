package com.example.examer.data.domain

import android.net.Uri

/**
 * Models an audio file.
 * @param localAudioFileUri [Uri] for the locally saved audio file.
 * @param numberOfRepeatsAllowedForAudioFile indicates the maximum
 * number of repeats allowed for the audio file.
 */
data class ExamerAudioFile(
    val localAudioFileUri: Uri,
    val numberOfRepeatsAllowedForAudioFile: Int,
)