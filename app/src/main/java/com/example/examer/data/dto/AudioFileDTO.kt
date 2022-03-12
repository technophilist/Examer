package com.example.examer.data.dto

import com.example.examer.data.domain.ExamerAudioFile
import java.net.URL

/**
 * A DTO object equivalent of [ExamerAudioFile]
 */
data class AudioFileDTO(
    val audioFileUrl: URL,
    val numberOfRepeatsAllowedForAudioFile: Int,
)