package com.dogukan.tellme.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.repository.LatestRepository
import com.dogukan.tellme.repository.LatestRepositoryI

class LatestMessagesViewModel : ViewModel() ,LatestRepositoryI{
    private  var latestRepository = LatestRepository(this)
    val latestMessage = MutableLiveData<List<ChatMessage>>()
    val userLoading = MutableLiveData<Boolean>()
    val informationMessage = MutableLiveData<Boolean>()

    fun listenForLatestMessages(){
        latestRepository.listenForLatestMessages()


    }
    fun refreshRecyclerViewMessage(){
        latestRepository.refreshRecyclerViewMessages()
    }
    fun currentUser() {
        latestRepository.fetchCurrentUser()
    }
    override fun showLatestMessage(message: ArrayList<ChatMessage>) {
        latestMessage.value = message
        if (message.size==0){
            Log.d("MessageEmpty","EmptyMessage")
            informationMessage.value = true
        }
        else{
            informationMessage.value = false

            Log.d("MessageEmpty","EmptyMessage2")
        }
        userLoading.value = false
    }

    override fun getLastUserInfo(userList: ArrayList<Users>) {
        TODO("Not yet implemented")
    }

}