package com.example.boxchat.chatmessage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.boxchat.databinding.MessageBinding
import com.example.boxchat.model.ChatMessageModel

class ChatBoxAdapter(private var messageList : ArrayList<ChatMessageModel>)
    : RecyclerView.Adapter<ChatBoxAdapter.MyViewHolder>() {

    fun addNewMessage(list : ArrayList<ChatMessageModel>) {
        messageList = list
        notifyDataSetChanged()
    }

    class MyViewHolder (
        private val messageBinding: MessageBinding
            ) : RecyclerView.ViewHolder(messageBinding.root) {
                fun bind(data : ChatMessageModel) {
                    with(messageBinding) {
                        messageUser.text = data.messageUser
                        messageText.text = data.messageText
                        messageTime.text = data.messageTime
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val messageItems = MessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = MyViewHolder(messageItems)
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}