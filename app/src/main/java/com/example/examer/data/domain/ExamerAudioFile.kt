package com.example.examer.data.domain

import android.net.Uri
import com.example.examer.data.domain.serializers.UriSerializer
import kotlinx.serialization.Serializable

/**
 * Models an audio file.
 * @param localAudioFileUri [Uri] for the locally saved audio file.
 * @param numberOfRepeatsAllowedForAudioFile indicates the maximum
 * number of repeats allowed for the audio file.
 */
@Serializable
data class ExamerAudioFile(
    val localAudioFileUri: @Serializable(with = UriSerializer::class) Uri,
    val numberOfRepeatsAllowedForAudioFile: Int,
) : java.io.Serializable
