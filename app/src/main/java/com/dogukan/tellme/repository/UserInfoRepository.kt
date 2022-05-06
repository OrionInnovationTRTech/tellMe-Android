package com.dogukan.tellme.repository

import android.annotation.SuppressLint
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.util.AppUtil
import com.google.firebase.database.*

class UserInfoRepository()  {

    private lateinit var databaseRef : DatabaseReference
    private var liveData : MutableLiveData<Users>?=null
    private val appUtil = AppUtil()

     fun getUser() : LiveData<Users>{

        if (liveData == null){
            liveData = MutableLiveData()
        }
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUID()!!)
        databaseRef.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NullSafeMutableLiveData")
            override fun onDataChange(snapshot: DataSnapshot) {
                val userModel = snapshot.getValue(Users::class.java)
                liveData?.value = userModel
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        return liveData!!
    }

    object StaticFunction{
        private var instance : UserInfoRepository?= null
        fun getInstance() : UserInfoRepository{
            if (instance==null){
                instance = UserInfoRepository()
            }
            return instance!!
        }
    }
}