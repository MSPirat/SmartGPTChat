package com.example.smartchatgpt

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val messageList: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val chatView =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_item, null)
        return MyViewHolder(chatView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = messageList[position]
        if (message.sendBy == Message.SEND_BY_ME) {
            holder.startChatView.visibility = View.GONE
            holder.endChatView.visibility = View.VISIBLE
            holder.endTextView.text = message.message
        } else {
            holder.endChatView.visibility = View.GONE
            holder.startChatView.visibility = View.VISIBLE
            holder.startTextView.text = message.message
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var startChatView: LinearLayout = itemView.findViewById(R.id.start_chat_view)
        var endChatView: LinearLayout = itemView.findViewById(R.id.end_chat_view)
        var startTextView: TextView = itemView.findViewById(R.id.start_chat_text_view)
        var endTextView: TextView = itemView.findViewById(R.id.end_chat_text_view)
    }
}