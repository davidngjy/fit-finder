package com.sample.fitfinder.data

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.sample.fitfinder.proto.UserProfile
import java.io.InputStream
import java.io.OutputStream

object UserProfileSerializer : Serializer<UserProfile> {
    override fun readFrom(input: InputStream): UserProfile {
        try {
            return UserProfile.parseFrom(input)
        } catch (ex: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read UserProfile data store", ex)
        }
    }

    override fun writeTo(t: UserProfile, output: OutputStream)
        = t.writeTo(output)
}