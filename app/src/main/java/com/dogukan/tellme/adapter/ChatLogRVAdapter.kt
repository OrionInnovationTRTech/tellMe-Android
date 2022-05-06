package com.dogukan.tellme.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.dogukan.tellme.R
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.google.firebase.auth.FirebaseAuth

class ChatLogRVAdapter(private val chatmessages : ArrayList<ChatMessage> ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        var SENDER_VIEW_TYPE = 1
        var RECEIVER_VIEW_TYPE = 2
    }

    class SenderViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var sendermessage: TextView = itemView.findViewById(R.id.chat_to_row_TV)
        var senderTimeStamp = itemView.findViewById<TextView>(R.id.Time_stamp_chat_to_row_TV)
    }
    class RecieverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var recievermessage: TextView = itemView.findViewById(R.id.chat_from_TV)
        var recieverTimeStamp = itemView.findViewById<TextView>(R.id.Time_stamp_chat_from_row_TV)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType== SENDER_VIEW_TYPE){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_to_row,parent,false)
            SenderViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_from_row,parent,false)
            RecieverViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatmessages.get(position).fromID.equals(FirebaseAuth.getInstance().uid)){
            SENDER_VIEW_TYPE
        } else{
            RECEIVER_VIEW_TYPE
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.itemViewType == SENDER_VIEW_TYPE) {
            (holder as SenderViewHolder).sendermessage.text = chatmessages[position].text
            Log.d("timestamp",chatmessages[position].TimeStamp)
            holder.senderTimeStamp.text = chatmessages[position].TimeStamp
        } else {
            (holder as RecieverViewHolder).recievermessage.text = chatmessages[position].text
            holder.recieverTimeStamp.text = chatmessages[position].TimeStamp
            Log.d("timestamp",chatmessages[position].TimeStamp)
        }
    }
    override fun getItemCount(): Int {
        return chatmessages.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun ChatMessageUpdate(NewUserList : List<ChatMessage>){
        chatmessages.clear()
        chatmessages.addAll(NewUserList)
        notifyDataSetChanged()
    }

}