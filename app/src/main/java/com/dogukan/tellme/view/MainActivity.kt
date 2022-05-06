package com.dogukan.tellme.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.dogukan.tellme.R
import com.dogukan.tellme.util.Addition
import com.dogukan.tellme.util.AppUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_latest_messages.*


class MainActivity : AppCompatActivity() {

    private var firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    private val appUtil = AppUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*if(firebaseAuth.currentUser==null){
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val myFragment = RegisterFragment()
            transaction.replace(R.id.fragmentContainerView, myFragment)
            //transaction.addToBackStack(null)
            transaction.commit()
        }
        else{

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val myFragment = LatestMessagesFragment()
            transaction.replace(R.id.fragmentContainerView, myFragment)
            //transaction.addToBackStack(null)
            transaction.commit()

        }*/
        /*
            FirebaseMessaging.getInstance().token.addOnSuccessListener {

                    val token = it
                    val databaseReference =
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(AppUtil.getUID()!!)
                    val map: MutableMap<String, Any> = HashMap()
                    map["token"] = token
                    databaseReference.updateChildren(map)

            }*/


        }
    fun activeState(activeState : String){
        if (firebaseAuth.currentUser!=null){
            val ref = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUID()!!)
            val hashMap : MutableMap<String,String> = HashMap()
            hashMap["activeState"] = activeState
            ref.updateChildren(hashMap as Map<String, Any>)
        }



    }

    override fun onStart() {
        super.onStart()
        activeState("online")
    }
    override fun onResume() {
        super.onResume()
        activeState("online")
    }

    override fun onPause() {
        super.onPause()
        activeState("offline")

    }

    override fun onStop() {
        super.onStop()
        activeState("offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        activeState("offline")
    }




    }
