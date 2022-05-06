package com.dogukan.tellme.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.repository.UserRepository
import com.dogukan.tellme.repository.UserRepositoryI

class UserListViewModel() : ViewModel(), UserRepositoryI {
    private var userRepository = UserRepository(this)
    val users = MutableLiveData<List<Users>>()
    val userLoading = MutableLiveData<Boolean>()
    val informationMessage = MutableLiveData<Boolean>()

    fun getAllUsers() : LiveData<List<Users>>{
        return users
    }
    fun getUser(){
        userRepository.getUserFirebase()
    }
    override fun showListOfUser(userList: ArrayList<Users>) {
        users.value = userList
        if (userList.size==0){
            Log.d("MessageEmpty","EmptyMessage")
            informationMessage.value = true
        }
        else{
            informationMessage.value = false

            Log.d("MessageEmpty","EmptyMessage2")
        }
        userLoading.value = false
    }
}