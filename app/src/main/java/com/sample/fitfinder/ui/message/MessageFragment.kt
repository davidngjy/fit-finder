package com.sample.fitfinder.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.fitfinder.data.repository.MessageRepository
import com.sample.fitfinder.databinding.FragmentMessageBinding

class MessageFragment : Fragment() {

    private lateinit var messageViewModel: MessageViewModel
    private lateinit var binding: FragmentMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageBinding.inflate(inflater)

        val viewModelFactory = MessageViewModelFactory(MessageRepository())
        messageViewModel = ViewModelProvider(this, viewModelFactory)
            .get(MessageViewModel::class.java)

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
        messageViewModel.navigateToMessageRoom.observe(viewLifecycleOwner, { roomId ->
            roomId?.let {
                val action = MessageFragmentDirections
                    .actionMessageFragmentToMessageRoomFragment(it)
                this.findNavController()
                    .navigate(action)

                messageViewModel.onMessageRoomNavigated()
            }
        })

        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.roomList.layoutManager = manager

        return binding.root
    }
}