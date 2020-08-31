package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sample.fitfinder.R
import com.sample.fitfinder.ui.session.viewmodel.SessionUpcomingViewModel

class SessionUpcomingFragment : Fragment() {

    private lateinit var viewModel: SessionUpcomingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_session_upcoming, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SessionUpcomingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}