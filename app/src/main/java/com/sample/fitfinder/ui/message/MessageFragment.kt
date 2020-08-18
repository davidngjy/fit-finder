package com.sample.fitfinder.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sample.fitfinder.R
import com.sample.fitfinder.databinding.FragmentMessageBinding

class MessageFragment : Fragment() {

    private lateinit var messageViewModel: MessageViewModel
    private lateinit var binding: FragmentMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_message,
            container,
            false
        )

        messageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)

        messageViewModel.text.observe(viewLifecycleOwner, {
            binding.textMessage.text = it
        })

        return binding.root
    }
}