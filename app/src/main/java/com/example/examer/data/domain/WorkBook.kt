package com.example.examer.data.domain

/**
 * A class that models a work book with the associated [id],
 * [audioFile] and [questions] list.
 */
data class WorkBook(
    val id: String,
    val audioFile: ExamerAudioFile,
    val questions: List<MultiChoiceQuestion>
)