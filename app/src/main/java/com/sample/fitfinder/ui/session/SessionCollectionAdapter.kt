package com.sample.fitfinder.ui.session

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sample.fitfinder.proto.UserProfile
import kotlin.time.ExperimentalTime

class SessionCollectionAdapter(fragment: Fragment, private val userRole: UserProfile.UserRole)
    : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int =
        if (userRole == UserProfile.UserRole.Admin || userRole == UserProfile.UserRole.Trainer) 3
        else 2

    @ExperimentalTime
    override fun createFragment(position: Int): Fragment {
        return if (userRole == UserProfile.UserRole.Admin || userRole == UserProfile.UserRole.Trainer) {
            when (position) {
                0 -> SessionAvailableFragment()
                1 -> SessionUpcomingFragment()
                2 -> SessionPastFragment()
                else -> throw IllegalArgumentException("Unknown fragment")
            }
        } else {
            when (position) {
                0 -> SessionUpcomingFragment()
                1 -> SessionPastFragment()
                else -> throw IllegalArgumentException("Unknown fragment")
            }
        }
    }
}