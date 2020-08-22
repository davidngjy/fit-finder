package com.sample.fitfinder.ui.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sample.fitfinder.databinding.ItemMessageListBinding
import com.sample.fitfinder.domain.MessageUserList

class MessageAdapter(private val clickListener: MessageListItemListener)
    : ListAdapter<MessageUserList, MessageAdapter.ViewHolder>(MessageListDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class ViewHolder private constructor(private val binding: ItemMessageListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MessageUserList, clickListener: MessageListItemListener) {
            binding.message = item
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ItemMessageListBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class MessageListDiffCallBack: DiffUtil.ItemCallback<MessageUserList>() {
    override fun areItemsTheSame(oldItem: MessageUserList, newItem: MessageUserList): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MessageUserList, newItem: MessageUserList): Boolean {
        return oldItem == newItem
    }
}

class MessageListItemListener(val clickListener: (RoomId: Long) -> Unit) {
    fun onClick(message: MessageUserList) = clickListener(message.id)
}