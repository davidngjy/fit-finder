package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.databinding.FragmentSessionAvailableBinding
import kotlin.time.ExperimentalTime

class SessionAvailableFragment : Fragment() {

    private lateinit var sessionAvailableViewModel: SessionAvailableViewModel
    private lateinit var binding: FragmentSessionAvailableBinding

    @ExperimentalTime
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionAvailableBinding.inflate(inflater)

        val viewModelFactory = SessionAvailableViewModelFactory(SessionRepository())
        sessionAvailableViewModel = ViewModelProvider(this, viewModelFactory)
            .get(SessionAvailableViewModel::class.java)

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