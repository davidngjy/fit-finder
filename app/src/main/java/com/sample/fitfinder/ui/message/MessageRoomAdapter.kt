package com.sample.fitfinder.ui.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sample.fitfinder.databinding.ItemMyMessageBinding
import com.sample.fitfinder.databinding.ItemOtherMessageBinding
import com.sample.fitfinder.domain.Message
import com.sample.fitfinder.domain.MessageSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val MY_MESSAGE = 0
private const val OTHER_MESSAGE = 1

class MessageRoomAdapter : ListAdapter<DataItem, RecyclerView.ViewHolder>(MessageDiffCallBack()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) as DataItem.MessageItem
        return when (item.message.messageSender) {
            MessageSender.ME -> MY_MESSAGE
            MessageSender.OTHER -> OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MY_MESSAGE -> MyMessageViewHolder.from(parent)
            OTHER_MESSAGE -> OtherMessageViewHolder.from(parent)
            else -> throw IllegalArgumentException("Unknown View Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyMessageViewHolder -> {
                val messageItem = getItem(position) as DataItem.MessageItem
                holder.bind(messageItem.message)
            }
            is OtherMessageViewHolder -> {
                val messageItem = getItem(position) as DataItem.MessageItem
                holder.bind(messageItem.message)
            }
        }
    }

    // Triggers from view model (to observe and submit new change)
    fun wrapAndSubmitList(list: List<Message>?) {
        adapterScope.launch {
            val items = list?.map { DataItem.MessageItem(it) }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    // VIEW HOLDERS
    class MyMessageViewHolder private constructor(private val binding: ItemMyMessageBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Message) {
            binding.message = item
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMyMessageBinding
                    .inflate(layoutInflater, parent, false)
                return MyMessageViewHolder(binding)
            }
        }
    }

    class OtherMessageViewHolder private constructor(private val binding: ItemOtherMessageBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Message) {
            binding.message = item
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOtherMessageBinding
                    .inflate(layoutInflater, parent, false)
                return OtherMessageViewHolder(binding)
            }
        }
    }
}

class MessageDiffCallBack: DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

sealed class DataItem {
    abstract val id: Long

    data class MessageItem(val message: Message): DataItem() {
        override val id: Long = message.id
    }
}