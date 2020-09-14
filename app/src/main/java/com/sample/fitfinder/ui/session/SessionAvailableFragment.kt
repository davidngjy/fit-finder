package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.fitfinder.databinding.FragmentSessionAvailableBinding
import com.sample.fitfinder.ui.session.viewmodel.SessionAvailableViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
@ExperimentalTime
class SessionAvailableFragment : Fragment() {

    private val sessionAvailableViewModel: SessionAvailableViewModel by viewModels()
    private lateinit var binding: FragmentSessionAvailableBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionAvailableBinding.inflate(inflater, container, false)

        val adapter = SessionAvailableAdapter(SessionListItemListener { sessionId ->
            sessionAvailableViewModel.onSessionClick(sessionId)
        })

        binding.sessionAvailableList.adapter = adapter
        binding.lifecycleOwner = this

        sessionAvailableViewModel.sessions.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        sessionAvailableViewModel.navigateToSessionDetail.observe(viewLifecycleOwner, {
            it?.let {
                findNavController()
                    .navigate(SessionFragmentDirections.actionSessionFragmentToSessionDetailFragment(it))
                sessionAvailableViewModel.onSessionDetailNavigated()
            }
        })

        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.sessionAvailableList.layoutManager = manager

        return binding.root
    }
}