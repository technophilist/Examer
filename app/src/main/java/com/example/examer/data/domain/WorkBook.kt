package com.example.examer.data.domain

import kotlinx.serialization.Serializable

/**
 * A class that models a work book with the associated [id],
 * [audioFile] and [questions] list.
 */
@Serializable
data class WorkBook(
    val id: String,
    val audioFile: ExamerAudioFile,
    val questions: List<MultiChoiceQuestion>
) : java.io.Serializable