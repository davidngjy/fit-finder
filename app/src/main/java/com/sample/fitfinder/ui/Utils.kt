package com.sample.fitfinder.ui

import android.content.Context
import android.content.res.Configuration
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.sample.fitfinder.R

fun GoogleMap.configureDayNightStyle(context: Context) {
    val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
        setMapStyle(MapStyleOptions(context.getString(R.string.map_night)))
    } else {
        setMapStyle(MapStyleOptions(context.getString(R.string.map_day)))
    }
}