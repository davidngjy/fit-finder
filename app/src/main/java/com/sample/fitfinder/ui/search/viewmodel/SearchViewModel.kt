package com.sample.fitfinder.ui.search.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.LatLngBounds
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.data.repository.SettingRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import java.time.ZoneId
import kotlin.time.ExperimentalTime

@FragmentScoped
@ExperimentalTime
@FlowPreview
@ExperimentalCoroutinesApi
class SearchViewModel @ViewModelInject constructor(
    sessionRepository: SessionRepository,
    settingRepository: SettingRepository)
    : ViewModel() {

    private val boundsChannel = BroadcastChannel<LatLngBounds>(Channel.CONFLATED)
    val availableSessions = boundsChannel
        .asFlow()
        .flatMapLatest {
            sessionRepository.getAvailableSessionByMapBound(it)
        }
        .combine(settingRepository.sessionFilter) { sessions, setting ->
            val initialFilteredSessions =
                sessions.filter { session ->
                    val sessionDate = session.sessionDateTime.atZone(ZoneId.systemDefault()).toLocalDate()
                    val sessionTime = session.sessionDateTime.atZone(ZoneId.systemDefault()).toLocalTime()

                    val upperDate = setting.upperDateTime.atZone(ZoneId.systemDefault()).toLocalDate()
                    val upperTime = setting.upperDateTime.atZone(ZoneId.systemDefault()).toLocalTime()

                    val lowerDate = setting.lowerDateTime.atZone(ZoneId.systemDefault()).toLocalDate()
                    val lowerTime = setting.lowerDateTime.atZone(ZoneId.systemDefault()).toLocalTime()

                    session.price <= setting.maxPrice
                    && session.duration >= setting.lowerDuration
                    && session.duration <= setting.upperDuration
                    && sessionDate >= lowerDate
                    && sessionDate <= upperDate
                    && sessionTime >= lowerTime
                    && sessionTime <= upperTime
                }

            return@combine if (setting.online && setting.inPerson) {
                initialFilteredSessions
            } else if (setting.online) { // Only online session
                initialFilteredSessions.filter {
                    it.isOnline
                }
            } else { // Only in person session
                initialFilteredSessions.filter {
                    it.isInPerson
                }
            }
        }
        .asLiveData()

    fun setBounds(bounds: LatLngBounds) {
        boundsChannel.offer(bounds)
    }
}