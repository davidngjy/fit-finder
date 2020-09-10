package com.sample.fitfinder.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.fitfinder.data.repository.MessageRepository
import com.sample.fitfinder.databinding.FragmentMessageRoomBinding
import com.sample.fitfinder.ui.message.viewmodel.MessageRoomViewModel
import com.sample.fitfinder.ui.message.viewmodel.MessageRoomViewModelFactory

class MessageRoomFragment : Fragment() {

    private val messageRoomViewModel: MessageRoomViewModel by viewModels { MessageRoomViewModelFactory(MessageRepository()) }
    private val args: MessageRoomFragmentArgs by navArgs()

    private lateinit var binding: FragmentMessageRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageRoomBinding.inflate(inflater, container, false)

        setActionBarTitle(args.message.senderDisplayName)

        val adapter = MessageRoomAdapter()
        binding.messageList.adapter = adapter
        binding.lifecycleOwner = this

        messageRoomViewModel.messages.observe(viewLifecycleOwner, {
            it?.let {
                adapter.wrapAndSubmitList(it)
            }
        })

        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.messageList.layoutManager = manager

        return binding.root
    }

    private fun setActionBarTitle(displayName: String) {
        (activity as AppCompatActivity).supportActionBar?.title = displayName
    }
}