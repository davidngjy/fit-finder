package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.fitfinder.databinding.FragmentSessionUpcomingBinding
import com.sample.fitfinder.ui.session.viewmodel.SessionUpcomingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
@ExperimentalTime
class SessionUpcomingFragment : Fragment() {

    private val viewModel: SessionUpcomingViewModel by viewModels()
    private lateinit var binding: FragmentSessionUpcomingBinding
    @Inject lateinit var adapter: SessionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionUpcomingBinding.inflate(inflater, container, false)

        binding.sessionUpcomingList.adapter = adapter
        binding.lifecycleOwner = this
        binding.sessionUpcomingList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        viewModel.sessions.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}