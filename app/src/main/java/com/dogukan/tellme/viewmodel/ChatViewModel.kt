package com.dogukan.tellme.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dogukan.tellme.databinding.FragmentChatLogBinding
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.repository.ChatRepository
import com.dogukan.tellme.repository.ChatRepositoryI

class ChatViewModel() : ViewModel(), ChatRepositoryI {
    private var chatRepository = ChatRepository(this)
    val message = MutableLiveData<List<ChatMessage>>()
    val activeState = MutableLiveData<Boolean>()
    val isSeen = MutableLiveData<Boolean>()


    fun getActiveState() : LiveData<Boolean>{
        return activeState
    }
    fun getActiveStateFirebase (ToID: String){
        chatRepository.getActiveState(ToID)
    }
    fun getAllMessage() : LiveData<List<ChatMessage>> {
        return message
    }

    fun getMessageFirebase(ToID : String){
        chatRepository.listenForMessage(ToID)

    }
    fun getIsSeenStatus() : LiveData<Boolean>{
        return isSeen
    }
    fun performSendMessage(text : String, toID: String, binding: FragmentChatLogBinding){
        chatRepository.performSendMessage(text,toID,binding)
    }

    fun checkIsSeen(toID: String){
        chatRepository.seenMessage(toID)
    }
    override fun showListOfMessage(messageList: ArrayList<ChatMessage>) {

        message.value = messageList
    }

    override fun checkActiveState(activeState: String){
        this.activeState.value = activeState.equals("online")

    }

    override fun checkIsSeen(isSeen: Boolean) {
        this.isSeen.value = isSeen
    }


    override fun sendMessage() {

    }
}