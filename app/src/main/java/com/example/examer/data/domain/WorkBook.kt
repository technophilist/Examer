package com.example.examer.data.domain

import android.net.Uri

/**
 * A class that models a work book, which is basically
 * a list of [MultiChoiceQuestion]s.
 */
data class WorkBook(
    val id: String,
    val audioFile: ExamerAudioFile,
    val questions: List<MultiChoiceQuestion>
)