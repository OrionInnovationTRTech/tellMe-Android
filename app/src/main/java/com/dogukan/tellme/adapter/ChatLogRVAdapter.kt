package com.dogukan.tellme.adapter

import android.annotation.SuppressLint
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dogukan.tellme.R
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.util.Addition
import com.dogukan.tellme.util.AppUtil
import com.google.firebase.auth.FirebaseAuth

class ChatLogRVAdapter(private val chatmessages : ArrayList<ChatMessage> ,private val recyclerDetails: RecyclerDetails) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var addition = Addition()
    private var appUtil = AppUtil()
    companion object{
        var SENDER_VIEW_TYPE = 1
        var RECEIVER_VIEW_TYPE = 2
    }

    class SenderViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var senderTrashImage : ImageView = itemView.findViewById(R.id.deleteToImageViewImage)
        var senderTrashMessage : ImageView = itemView.findViewById(R.id.deleteToImageViewMessage)
        var senderImage : ImageView = itemView.findViewById(R.id.image_to_imageView)
        var senderLinearLayout : LinearLayout = itemView.findViewById(R.id.layout_Image_To)
        var senderConstraintLayout : ConstraintLayout = itemView.findViewById(R.id.layout_Text_To)
        var sendermessage: TextView = itemView.findViewById(R.id.chat_to_row_TV)
        var senderTimeStamp = itemView.findViewById<TextView>(R.id.Time_stamp_chat_to_row_TV)
        var senderTimeStampImage = itemView.findViewById<TextView>(R.id.Time_stamp_chat_to_row_image_TV)
        var senderIsSeen = itemView.findViewById<TextView>(R.id.seenMessage_to_TV)
    }
    class RecieverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var recieverTrashImage : ImageView = itemView.findViewById(R.id.deleteFromImageViewImage)
        var recieverTrashMessage : ImageView = itemView.findViewById(R.id.deleteFromImageViewMessage)
        var recieverImage : ImageView = itemView.findViewById(R.id.image_from_imageView)
        var recieverLinearLayout : LinearLayout = itemView.findViewById(R.id.layout_Image_from)
        var recieverConstraintLayout : ConstraintLayout = itemView.findViewById(R.id.layout_Text_from)
        var recievermessage: TextView = itemView.findViewById(R.id.chat_from_TV)
        var recieverTimeStamp = itemView.findViewById<TextView>(R.id.Time_stamp_chat_from_row_TV)
        var recieverTimeStampImage = itemView.findViewById<TextView>(R.id.Time_stamp_chat_from_row_image_TV)
        var reciverIsSeen = itemView.findViewById<TextView>(R.id.seenMessage_from_TV)
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
            (holder as SenderViewHolder).senderIsSeen.visibility = View.VISIBLE

                if (chatmessages[position].type == "TEXT"){
                    (holder as SenderViewHolder).senderLinearLayout.visibility = View.GONE
                    holder.senderConstraintLayout.visibility = View.VISIBLE
                    holder.sendermessage.text = chatmessages[position].text
                    holder.senderTimeStamp.text = chatmessages[position].TimeStamp
                    holder.sendermessage.setOnLongClickListener {
                        if (chatmessages[position].fromID.equals(appUtil.getUID())) {
                            Log.d("OnLong", "Selam")
                            holder.senderTrashMessage.visibility = View.VISIBLE

                        }
                        return@setOnLongClickListener true
                    }
                    holder.senderTrashMessage.setOnClickListener {
                            recyclerDetails.onClickDeleteImageViewSender(holder)
                            holder.senderTrashMessage.visibility = View.GONE
                            holder.sendermessage.text = "Message Deleted."

                    }

                    //recyclerDetails.showIsSeenSender(holder,chatmessages,position)

                }
                else{
                    (holder as SenderViewHolder).senderLinearLayout.visibility = View.VISIBLE
                    holder.senderConstraintLayout.visibility = View.GONE
                    addition.picassoUseIt(chatmessages[position].text,holder.senderImage)
                    holder.senderTimeStampImage.text = chatmessages[position].TimeStamp
                    holder.sendermessage.setOnLongClickListener {
                        if (chatmessages[position].fromID.equals(appUtil.getUID())) {
                            Log.d("OnLong", "Selam")
                            holder.senderTrashImage.visibility = View.VISIBLE
                        }
                        return@setOnLongClickListener true
                    }
                    //recyclerDetails.showIsSeenSender(holder,chatmessages,position)

                }

            if (position == chatmessages.size-1){
                holder.senderIsSeen.visibility = View.VISIBLE

                if (chatmessages[position].isSeen){

                    holder.senderIsSeen.text = "seen"
                }else{
                    holder.senderIsSeen.text = "delivered"
                }
            }
            else{
                holder.senderIsSeen.visibility = View.GONE
            }


        } else {

            if (chatmessages[position].type == "TEXT"){

                (holder as RecieverViewHolder).recieverLinearLayout.visibility = View.GONE
                holder.recieverConstraintLayout.visibility = View.VISIBLE
                holder.recievermessage.text = chatmessages[position].text
                holder.recieverTimeStamp.text = chatmessages[position].TimeStamp
                holder.recievermessage.setOnLongClickListener {
                    if (chatmessages[position].fromID.equals(appUtil.getUID())) {
                        Log.d("OnLong", "Selam")
                        holder.recieverTrashMessage.visibility = View.VISIBLE

                    }
                    return@setOnLongClickListener true
                }
                holder.recieverTrashMessage.setOnClickListener {
                    recyclerDetails.onClickDeleteImageViewReciever(holder as RecieverViewHolder)
                    holder.recieverTrashMessage.visibility = View.GONE
                    holder.recievermessage.text = "Message Deleted."

                }

            }
            else{
                (holder as RecieverViewHolder).recieverLinearLayout.visibility = View.VISIBLE
                holder.recieverConstraintLayout.visibility = View.GONE
                addition.picassoUseIt(chatmessages[position].text,holder.recieverImage)
                holder.recieverTimeStampImage.text = chatmessages[position].TimeStamp
                holder.recievermessage.setOnLongClickListener {
                    if (chatmessages[position].fromID.equals(appUtil.getUID())) {
                        Log.d("OnLong", "Selam")
                        holder.recieverTrashImage.visibility = View.VISIBLE
                    }
                    return@setOnLongClickListener true
                }
            }
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


    interface RecyclerDetails{
        fun onClickDeleteImageViewReciever(holder: RecieverViewHolder)
        fun onClickDeleteImageViewSender(holder: SenderViewHolder)
        fun showIsSeenSender(holder: SenderViewHolder,chatMessage : ArrayList<ChatMessage>,p: Int)

    }
}
