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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
@ExperimentalTime
class SessionAvailableFragment : Fragment() {

    private val viewModel: SessionAvailableViewModel by viewModels()
    private lateinit var binding: FragmentSessionAvailableBinding

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionAvailableBinding.inflate(inflater, container, false)

        val adapter = SessionAdapter(SessionListItemListener { sessionId ->
            viewModel.onSessionClick(sessionId)
        })

        binding.sessionAvailableList.adapter = adapter
        binding.lifecycleOwner = this
        binding.sessionAvailableList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        viewModel.sessions.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToSessionDetail.observe(viewLifecycleOwner, {
            it?.let {
                findNavController()
                    .navigate(SessionFragmentDirections.actionSessionFragmentToSessionDetailFragment(it))
                viewModel.onSessionDetailNavigated()
            }
        })

        return binding.root
    }
}