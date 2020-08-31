package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sample.fitfinder.R
import com.sample.fitfinder.data.repository.UserRepository
import com.sample.fitfinder.databinding.FragmentSessionBinding
import com.sample.fitfinder.domain.User
import com.sample.fitfinder.domain.UserRole

class SessionFragment : Fragment() {

    private lateinit var binding: FragmentSessionBinding
    private lateinit var currentUser: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_session,
            container,
            false
        )

        currentUser = UserRepository().getCurrentUser().value!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sessionCollectionAdapter = SessionCollectionAdapter(this, UserRepository())
        binding.viewPager.adapter = sessionCollectionAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if (currentUser.role == UserRole.ADMIN || currentUser.role == UserRole.TRAINER) {
                when (position) {
                    0 -> tab.setupTab(R.string.session_available, R.drawable.ic_session_available_24)
                    1 -> tab.setupTab(R.string.session_upcoming, R.drawable.ic_session_upcoming_24)
                    2 -> tab.setupTab(R.string.session_past, R.drawable.ic_session_past_24)
                }
            } else {
                when (position) {
                    0 -> tab.setupTab(R.string.session_upcoming, R.drawable.ic_session_upcoming_24)
                    1 -> tab.setupTab(R.string.session_past, R.drawable.ic_session_past_24)
                }
            }
        }.attach()
    }

    // Extension to Tab for setup
    private fun TabLayout.Tab.setupTab(textResId: Int, drawableIconRestId: Int) {
        setText(textResId)
        setIcon(drawableIconRestId)
    }
}