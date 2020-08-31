package com.sample.fitfinder.ui.session

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.sample.fitfinder.R
import com.sample.fitfinder.domain.Session
import java.math.RoundingMode
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@BindingAdapter("sessionHeadingFormatted")
fun TextView.setSessionHeading(item: Session?) {
    item?.let {
        val formatter: DateTimeFormatter = DateTimeFormatter
            .ofPattern("dd/MMM hh:mm a")
            .withLocale(Locale.UK)
            .withZone(ZoneId.systemDefault())

        val datetime = item.sessionDateTime
        val isOnline = item.isOnline
        val isInPerson = item.isInPerson

        val dateTimeString = formatter.format(datetime)

        val res = context.resources

        if (isOnline && isInPerson) {
            text = res.getString(R.string.session_heading, dateTimeString, "Online | In-Person")
        } else if (isOnline) {
            text = res.getString(R.string.session_heading, dateTimeString, "Online")
        } else {
            text = res.getString(R.string.session_heading, dateTimeString, "In-Person")
        }
    }
}

@ExperimentalTime
@BindingAdapter("sessionDurationFormatted")
fun TextView.setSessionDuration(item: Session?) {
    item?.let {
        text = item.durationInMin.toString(DurationUnit.MINUTES)
    }
}

@BindingAdapter("sessionPriceFormatted")
fun TextView.setSessionPrice(item: Session?) {
    item?.let {
        val roundedPrice = item.price.setScale(2, RoundingMode.HALF_DOWN).toString()
        text = roundedPrice
    }
}