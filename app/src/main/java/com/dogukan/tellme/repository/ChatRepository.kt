package com.dogukan.tellme.repository

import android.util.Log
import com.dogukan.tellme.databinding.FragmentChatLogBinding
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.util.AppUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class ChatRepository(ChatRepositoryI: ChatRepositoryI) {
    private var chatRepositoryI : ChatRepositoryI ?= ChatRepositoryI
    private var chatMessageList = ArrayList<ChatMessage>()
    private  var AppUtil = AppUtil()

     fun listenForMessage(toID : String){

        val fromID = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/${AppUtil.getUID()}/$toID")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    chatMessageList.add(chatMessage)
                    chatRepositoryI?.showListOfMessage(chatMessageList)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
     fun getActiveState(toID: String){
        val databaseref = FirebaseDatabase.getInstance().getReference("users").child(toID)
        databaseref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val activeState = snapshot.child("activeState").value.toString()
                chatRepositoryI?.checkActiveState(activeState)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

     fun performSendMessage(text : String, toID: String,binding: FragmentChatLogBinding){

        val fromID = FirebaseAuth.getInstance().uid
        val Ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()
        if (fromID==null){
            return
        }
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
         var timeStamp = ""

         timeStamp = String.format("%02d:%02d",hour,minute)

        val chatMessage = ChatMessage(Ref.key!!, text.toString(), fromID.toString(), toID.toString(), timeStamp)
        Ref.setValue(chatMessage)
            .addOnSuccessListener {
                binding.sendmassegeTV.text.clear()
                binding.recyclerView2.scrollToPosition(chatMessageList.count()-1)
            }
        toRef.setValue(chatMessage)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID/$toID")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toID/$fromID")
        latestMessageToRef.setValue(chatMessage)
         Log.d("user-messages",timeStamp)

    }

}