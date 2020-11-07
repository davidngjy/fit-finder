package com.sample.fitfinder.data

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.sample.fitfinder.proto.SearchFilter
import java.io.InputStream
import java.io.OutputStream

object SearchFilterSerializer : Serializer<SearchFilter> {
    override fun readFrom(input: InputStream): SearchFilter {
        try{
            return SearchFilter.parseFrom(input)
        } catch (ex: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read SearchFilter data store", ex)
        }
    }

    override fun writeTo(t: SearchFilter, output: OutputStream)
        = t.writeTo(output)
}