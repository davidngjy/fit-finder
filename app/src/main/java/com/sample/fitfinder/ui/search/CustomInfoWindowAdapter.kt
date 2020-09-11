package com.sample.fitfinder.ui.search

import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson
import com.sample.fitfinder.databinding.ItemInfoWindowBinding

class CustomInfoWindowAdapter(private val inflater: LayoutInflater, private val converter: Gson)
    : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker?) : View {
        val data = converter.fromJson(marker!!.title, MapInfoData::class.java)
        marker.tag = data.id

        val binding = ItemInfoWindowBinding.inflate(inflater)
        binding.data = data
        binding.executePendingBindings()

        return binding.root
    }

    override fun getInfoContents(marker: Marker?) : View? = null
}

data class MapInfoData (
    val id: Long,
    val title: String,
    val description: String,
    val dateTimeEpochMilli: Long
)