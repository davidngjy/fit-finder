package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sample.fitfinder.R
import com.sample.fitfinder.databinding.FragmentSessionBinding

class SessionFragment : Fragment() {

    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var binding: FragmentSessionBinding

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

        sessionViewModel = ViewModelProvider(this).get(SessionViewModel::class.java)

        sessionViewModel.text.observe(viewLifecycleOwner, {
            binding.textSession.text = it
        })

        return binding.root
    }
}