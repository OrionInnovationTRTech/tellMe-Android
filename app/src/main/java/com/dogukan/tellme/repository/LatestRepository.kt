package com.dogukan.tellme.repository

import android.util.Log
import com.dogukan.tellme.adapter.LatestMessagesRVAdapter
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.view.LatestMessagesFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LatestRepository(latestRepositoryI: LatestRepositoryI) {
    private var latestRepositoryI : LatestRepositoryI ?= latestRepositoryI
    private var latestMessageList = ArrayList<ChatMessage>()
    val latestMessagesMap = HashMap<String, ChatMessage>()


    fun listenForLatestMessages(){
        val fromID = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage!=null){
                    latestMessagesMap[snapshot.key!!]=chatMessage
                    latestRepositoryI?.showLatestMessage(latestMessageList)
                    refreshRecyclerViewMessages()
                }

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                latestMessageList.clear()
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage!=null){
                    latestMessagesMap[snapshot.key!!]=chatMessage
                    latestRepositoryI?.showLatestMessage(latestMessageList)
                    refreshRecyclerViewMessages()
                }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                //refreshRecyclerViewMessages()

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //refreshRecyclerViewMessages()

            }
            override fun onCancelled(error: DatabaseError) {

            }

        })


    }
    fun refreshRecyclerViewMessages(){
        latestMessageList.clear()
        latestMessagesMap.values.forEach {
            latestMessageList.add(it)
        }
        latestRepositoryI?.showLatestMessage(latestMessageList)
    }


    fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                LatestMessagesFragment.users = snapshot.getValue(Users::class.java)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



}