package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sample.fitfinder.R
import com.sample.fitfinder.ui.session.viewmodel.SessionUpcomingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionUpcomingFragment : Fragment() {

    private val viewModel: SessionUpcomingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_session_upcoming, container, false)
    }
}