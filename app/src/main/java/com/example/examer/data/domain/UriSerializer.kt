package com.example.examer.data.domain

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializer(forClass = Uri::class)
class UriSerializer : KSerializer<Uri> {
    override fun serialize(encoder: Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uri = Uri.parse(decoder.decodeString())
}