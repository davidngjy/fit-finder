package com.sample.fitfinder.ui.session

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sample.fitfinder.data.repository.UserRepository
import com.sample.fitfinder.databinding.ItemSessionListBinding
import com.sample.fitfinder.domain.Session
import com.sample.fitfinder.domain.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SessionAdapter @Inject constructor()
    : ListAdapter<Session, SessionAdapter.ViewHolder>(SessionDiffCallBack()) {

    @Inject lateinit var userRepository: UserRepository
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var clickListener: SessionListItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        scope.launch {
            val trainerProfile = userRepository.getUserProfile(item.trainerUserId).first()
            holder.bind(item, trainerProfile, clickListener)
        }
    }

    fun setClickListener(listener: SessionListItemListener) {
        clickListener = listener
    }

    class ViewHolder private constructor(private val binding: ItemSessionListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Session, trainerProfile: User, clickListener: SessionListItemListener?) {
            binding.trainerProfile = trainerProfile
            binding.session = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ItemSessionListBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class SessionDiffCallBack: DiffUtil.ItemCallback<Session>() {
    override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem.sessionId == newItem.sessionId
    }

    override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem == newItem
    }
}

class SessionListItemListener(val clickListener: (sessionId: Long) -> Unit) {
    fun onClick(sessionId: Long) = clickListener(sessionId)
}