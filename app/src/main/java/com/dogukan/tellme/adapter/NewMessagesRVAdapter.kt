package com.dogukan.tellme

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.util.Addition
import com.dogukan.tellme.view.NewMessagesFragmentDirections

import com.squareup.picasso.Picasso

class NewMessagesRVAdapter(private val userList: ArrayList<Users>) : RecyclerView.Adapter<NewMessagesRVAdapter.NewMessageViewHolder>(){
    private val addition = Addition()
    class NewMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView = itemView.findViewById(R.id.message_imageView)
        var itemTitle : TextView = itemView.findViewById(R.id.message_TV)
        var itemStatus: TextView = itemView.findViewById(R.id.status_TV)
        var itemProgressBar : ProgressBar = itemView.findViewById(R.id.new_messages_progressBar)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row_new_message,parent,false)
        return NewMessageViewHolder(view)
    }
    override fun getItemCount(): Int {
        return userList.size
    }
    override fun onBindViewHolder(holder: NewMessageViewHolder, position: Int){
        holder.itemProgressBar.visibility = View.VISIBLE
        holder.itemTitle.text = userList[position].username
        holder.itemStatus.text = userList[position].status

        addition.picassoUseIt(userList[position].profileImageURL,holder.itemImage,holder.itemProgressBar)


        //Picasso.get().load(userList[position].profileImageURL).into(holder.itemImage)
        holder.itemView.setOnClickListener {
            val action = NewMessagesFragmentDirections.actionNewMessagesFragmentToChatLogFragment(position, userList[position].uid,userList[position].username,userList[position].profileImageURL)
            Log.d("uid",userList[position].uid)
            Navigation.findNavController(it).navigate(action)
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun UsersListUpdate(NewUserList : List<Users>){
        userList.clear()
        userList.addAll(NewUserList)

        notifyDataSetChanged()
    }
}
