package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sample.fitfinder.R
import com.sample.fitfinder.data.repository.UserRepository
import com.sample.fitfinder.databinding.FragmentSessionBinding
import com.sample.fitfinder.domain.User
import com.sample.fitfinder.domain.UserRole
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SessionFragment : Fragment() {

    @Inject lateinit var userRepository: UserRepository

    private lateinit var binding: FragmentSessionBinding
    private lateinit var currentUser: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionBinding.inflate(inflater, container, false)

        currentUser = userRepository.getCurrentUser().value!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sessionCollectionAdapter = SessionCollectionAdapter(this, userRepository)

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

        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.tag == R.string.session_available) {
                    binding.sessionAddFab.show()
                } else {
                    binding.sessionAddFab.hide()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })

        binding.sessionAddFab.setOnClickListener {
            findNavController()
                .navigate(SessionFragmentDirections.actionSessionFragmentToSessionAddFragment())
        }
    }

    // Extension to Tab for setup
    private fun TabLayout.Tab.setupTab(textResId: Int, drawableIconRestId: Int) {
        setText(textResId)
        setIcon(drawableIconRestId)
        tag = textResId
    }
}