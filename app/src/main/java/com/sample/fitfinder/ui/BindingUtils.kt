package com.sample.fitfinder.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.sample.fitfinder.R
import com.sample.fitfinder.domain.Session
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@BindingAdapter("sessionHeadingFormatted")
fun TextView.setSessionHeading(item: Session?) {
    item?.let {
        val dateTime = item.sessionDateTime
        val isOnline = item.isOnline
        val isInPerson = item.isInPerson

        val dateTimeString = dateTimeConverter(dateTime)

        val res = context.resources

        text = if (isOnline && isInPerson) {
            res.getString(R.string.session_heading, dateTimeString, "Online | In-Person")
        } else if (isOnline) {
            res.getString(R.string.session_heading, dateTimeString, "Online")
        } else {
            res.getString(R.string.session_heading, dateTimeString, "In-Person")
        }
    }
}

@BindingAdapter("sessionDurationFormatted")
fun TextView.setSessionDuration(item: Session?) {
    item?.let {
        val res = context.resources
        text = res.getString(R.string.session_duration, item.durationInMin)
    }
}

@BindingAdapter("sessionCostFormatted")
fun TextView.setSessionCost(item: Session?) {
    item?.let {
        text = item.cost.toString()
    }
}

@BindingAdapter("dateTimeFormatted")
fun TextView.setDateTime(item: Instant?) {
    item?.let {
        text = dateTimeConverter(item)
    }
}

private fun dateTimeConverter(dateTime: Instant) : String {
    val formatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd/MMM hh:mm a")
        .withLocale(Locale.UK)
        .withZone(ZoneId.systemDefault())
    return formatter.format(dateTime)
}

@BindingAdapter("dateTimeEpochMilliFormatted")
fun TextView.setDateTimeFromEpochMilli(item: Long?) {
    item?.let {
        text = dateTimeConverter(Date(item).toInstant())
    }
}

@BindingAdapter("sessionTypeAvailableIcon")
fun ImageView.setSessionTypeAvailableIcon(isAvailable: Boolean?) {
    isAvailable?.let {
        if (isAvailable) setImageResource(R.drawable.ic_session_checked_24)
        else setImageResource(R.drawable.ic_session_cross_24)
    }
}

@BindingAdapter("loadImageFromUrl")
fun ImageView.loadImageFromUrl(url: String?) {
    url?.let {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_default_profile_24)
            .into(this)
    }
}
