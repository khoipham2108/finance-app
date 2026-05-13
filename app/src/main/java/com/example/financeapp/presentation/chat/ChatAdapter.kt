package com.example.financeapp.presentation.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp.data.model.ChatMessage
import com.example.financeapp.databinding.ItemChatMessageBinding

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.VH>(DiffCb()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val b: ItemChatMessageBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(msg: ChatMessage) {
            if (msg.isUser) {
                b.cardUser.visibility = View.VISIBLE
                b.cardBot.visibility = View.GONE
                b.tvUserMessage.text = msg.content
            } else {
                b.cardBot.visibility = View.VISIBLE
                b.cardUser.visibility = View.GONE
                b.tvBotMessage.text = msg.content
            }
        }
    }

    class DiffCb : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(o: ChatMessage, n: ChatMessage) = o.id == n.id
        override fun areContentsTheSame(o: ChatMessage, n: ChatMessage) = o == n
    }
}