package com.sample.fitfinder.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.fitfinder.data.repository.MessageRepository
import com.sample.fitfinder.databinding.FragmentMessageBinding
import com.sample.fitfinder.ui.message.viewmodel.MessageViewModel
import com.sample.fitfinder.ui.message.viewmodel.MessageViewModelFactory

class MessageFragment : Fragment() {

    private val messageViewModel: MessageViewModel by viewModels { MessageViewModelFactory(MessageRepository()) }
    private lateinit var binding: FragmentMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageBinding.inflate(inflater, container, false)

        // Setup adapter and click listener when user select a conversation (room)
        val adapter = MessageAdapter(MessageListItemListener { roomId ->
            messageViewModel.onMessageRoomClick(roomId)
        })
        binding.roomList.adapter = adapter
        binding.lifecycleOwner = this

        messageViewModel.rooms.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        // Navigate to message room
        messageViewModel.navigateToMessageRoom.observe(viewLifecycleOwner, { message ->
            message?.let {
                findNavController().navigate(MessageFragmentDirections
                        .actionMessageFragmentToMessageRoomFragment(message))

                messageViewModel.onMessageRoomNavigated()
            }
        })

        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.roomList.layoutManager = manager

        return binding.root
    }
}