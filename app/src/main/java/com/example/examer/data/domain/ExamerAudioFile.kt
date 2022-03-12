package com.example.examer.data.domain

import android.net.Uri

/**
 * Models an audio file.
 */
data class ExamerAudioFile(
    val localAudioFileUri: Uri,
    val numberOfRepeatsAllowedForAudioFile: Int,
)