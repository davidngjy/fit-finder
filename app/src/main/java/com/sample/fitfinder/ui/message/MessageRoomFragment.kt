package com.sample.fitfinder.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.fitfinder.data.repository.MessageRepository
import com.sample.fitfinder.databinding.FragmentMessageRoomBinding
import com.sample.fitfinder.ui.message.viewmodel.MessageRoomViewModel
import com.sample.fitfinder.ui.message.viewmodel.MessageRoomViewModelFactory

class MessageRoomFragment : Fragment() {

    private lateinit var messageRoomViewModel: MessageRoomViewModel
    private lateinit var binding: FragmentMessageRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageRoomBinding.inflate(inflater)

        val args = MessageRoomFragmentArgs.fromBundle(requireArguments())
        setActionBarTitle(args.message.senderDisplayName)

        val viewModelFactory = MessageRoomViewModelFactory(MessageRepository())
        messageRoomViewModel = ViewModelProvider(this, viewModelFactory)
            .get(MessageRoomViewModel::class.java)

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