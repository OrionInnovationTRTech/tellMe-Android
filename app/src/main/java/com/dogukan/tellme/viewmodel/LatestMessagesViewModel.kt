package com.dogukan.tellme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.repository.LatestRepository
import com.dogukan.tellme.repository.LatestRepositoryI

class LatestMessagesViewModel : ViewModel() ,LatestRepositoryI{
    private  var latestRepository = LatestRepository(this)
    val latestMessage = MutableLiveData<List<ChatMessage>>()
    val latestUser = MutableLiveData<List<Users>>()
    val userLoading = MutableLiveData<Boolean>()
    val informationMessage = MutableLiveData<Boolean>()

    fun listenForLatestMessages(){
        latestRepository.listenForLatestMessages()
    }

    fun refreshRecyclerViewUserInLatestMessage(){
        latestRepository.refreshRecyclerViewMessagesInUser()
    }
    fun refreshRecyclerViewMessage(){
        latestRepository.refreshRecyclerViewMessages()
    }
    fun currentUser() {
        latestRepository.fetchCurrentUser()
    }
    override fun showLatestMessage(message: ArrayList<ChatMessage>) {

        latestMessage.value = message
        informationMessage.value = message.size==0
        userLoading.value = false
    }
    fun getUserInfo() : LiveData<List<Users>>{
        return latestUser
    }
    override fun showUserInfoLatestMessage(user: ArrayList<Users>) {
        latestUser.value=user
    }

    override fun getLastUserInfo(userList: ArrayList<Users>) {
        TODO("Not yet implemented")
    }

}