package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.fitfinder.databinding.FragmentSessionPastBinding
import com.sample.fitfinder.ui.session.viewmodel.SessionPastViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
@ExperimentalTime
class SessionPastFragment : Fragment() {

    private val viewModel: SessionPastViewModel by viewModels()
    private lateinit var binding: FragmentSessionPastBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionPastBinding.inflate(inflater, container, false)

        val adapter = SessionAdapter(SessionListItemListener { sessionId ->
            // TODO implement click event
        })

        binding.sessionPastList.adapter = adapter
        binding.lifecycleOwner = this
        binding.sessionPastList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        viewModel.sessions.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}