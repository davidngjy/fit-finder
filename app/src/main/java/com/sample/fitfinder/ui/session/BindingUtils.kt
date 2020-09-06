package com.sample.fitfinder.ui.session

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.sample.fitfinder.R
import com.sample.fitfinder.domain.Session
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@BindingAdapter("sessionHeadingFormatted")
fun TextView.setSessionHeading(item: Session?) {
    item?.let {
        val formatter: DateTimeFormatter = DateTimeFormatter
            .ofPattern("dd/MMM hh:mm a")
            .withLocale(Locale.UK)
            .withZone(ZoneId.systemDefault())

        val dateTime = item.sessionDateTime
        val isOnline = item.isOnline
        val isInPerson = item.isInPerson

        val dateTimeString = formatter.format(dateTime)

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
