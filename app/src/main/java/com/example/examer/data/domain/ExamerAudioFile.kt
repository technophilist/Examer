package com.example.examer.data.domain

import android.net.Uri

/**
 * Models an audio file.
 */
data class ExamerAudioFile(
    val audioFileUri: Uri,
    val numberOfRepeatsAllowedForAudioFile: Int,
)