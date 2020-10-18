package com.sample.fitfinder.ui.search

import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.sample.fitfinder.databinding.ItemInfoWindowBinding
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CustomInfoWindowAdapter(private val inflater: LayoutInflater)
    : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker?) : View {
        val data = Json.decodeFromString<MapInfoData>(marker!!.title)
        marker.tag = data.id

        val binding = ItemInfoWindowBinding.inflate(inflater)
        binding.data = data
        binding.executePendingBindings()

        return binding.root
    }

    override fun getInfoContents(marker: Marker?) : View? = null
}

@Serializable
data class MapInfoData (
    val id: Long,
    val title: String,
    val description: String,
    val dateTimeEpochMilli: Long
)