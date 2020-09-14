package com.sample.fitfinder.data

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.sample.fitfinder.proto.GoogleToken
import java.io.InputStream
import java.io.OutputStream

object GoogleTokenSerializer : Serializer<GoogleToken> {
    override fun readFrom(input: InputStream): GoogleToken {
        try {
            return GoogleToken.parseFrom(input)
        } catch (ex: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read GoogleToken data store", ex)
        }
    }

    override fun writeTo(t: GoogleToken, output: OutputStream)
        = t.writeTo(output)
}