package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.databinding.FragmentSessionAvailableBinding
import com.sample.fitfinder.ui.session.viewmodel.SessionAvailableViewModel
import com.sample.fitfinder.ui.session.viewmodel.SessionAvailableViewModelFactory
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SessionAvailableFragment : Fragment() {

    private val sessionAvailableViewModel: SessionAvailableViewModel by viewModels { SessionAvailableViewModelFactory(SessionRepository) }
    private lateinit var binding: FragmentSessionAvailableBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionAvailableBinding.inflate(inflater, container, false)

        val adapter = SessionAvailableAdapter()
        binding.sessionAvailableList.adapter = adapter
        binding.lifecycleOwner = this

        sessionAvailableViewModel.sessions.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.sessionAvailableList.layoutManager = manager

        return binding.root
    }
}