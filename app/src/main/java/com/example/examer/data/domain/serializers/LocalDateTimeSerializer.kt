package com.example.examer.data.domain.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * A serializer for an instance of [LocalDateTime].
 * The instance will be serialized by converting it to its
 * corresponding milliseconds value. It will be deserialized
 * by converting the milliseconds value into a [LocalDateTime]
 * object.
 */
@Serializer(forClass = LocalDateTime::class)
class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override fun deserialize(decoder: Decoder): LocalDateTime {
        // decode the epoch millis
        val epochMillis = decoder.decodeLong()
        // get an instance of Instant using the epoch millis
        val instant = Instant.ofEpochMilli(epochMillis)
        // create an instance of LocalDateTime using the derived
        // Instant object
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        // convert LocalDateTime object to an instance of Instant
        // and convert that to EpochMills
        val epochMillis = value.toInstant(ZoneOffset.UTC).toEpochMilli()
        // encode the epoch millis value using encode
        encoder.encodeLong(epochMillis)
    }
}