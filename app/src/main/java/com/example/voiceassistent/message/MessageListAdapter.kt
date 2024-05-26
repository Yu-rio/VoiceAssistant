package com.example.voiceassistent.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.voiceassistent.R

class MessageListAdapter: RecyclerView.Adapter<MessageViewHolder>() {
    var messageList: MutableList<Message> = ArrayList()

    companion object {
        private const val ASSISTANT_TYPE = 0
        private const val USER_TYPE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View = if (viewType == USER_TYPE) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_message, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.assistant_message, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
    override fun getItemViewType(index: Int): Int {
        val message = messageList[index]
        return if (message.isSend) {
            USER_TYPE
        } else {
            ASSISTANT_TYPE
        }
    }
}