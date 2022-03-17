package com.example.examer.data.domain

import android.net.Uri
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Models an audio file.
 * @param localAudioFileUri [Uri] for the locally saved audio file.
 * @param numberOfRepeatsAllowedForAudioFile indicates the maximum
 * number of repeats allowed for the audio file.
 */
@Serializable
data class ExamerAudioFile(
    val localAudioFileUri: @Contextual Uri,
    val numberOfRepeatsAllowedForAudioFile: Int,
) : java.io.Serializable