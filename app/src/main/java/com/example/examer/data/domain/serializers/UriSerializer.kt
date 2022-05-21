package com.example.examer.data.domain.serializers

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

/**
 * A serializer for an instance of [Uri].
 * The instance will be serialized by encoding it as a [String].
 * It will be deserialized by parsing the string to a [Uri] object.
 */
@Serializer(forClass = Uri::class)
class UriSerializer : KSerializer<Uri> {
    override fun serialize(encoder: Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uri = Uri.parse(decoder.decodeString())
}