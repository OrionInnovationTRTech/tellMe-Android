package com.dogukan.tellme.models

import android.os.Parcelable
import android.widget.ProgressBar
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import com.dogukan.tellme.util.Addition
import com.dogukan.tellme.view.SettingsFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.parcel.Parcelize


@Parcelize
class Users(var uid : String , var username:String,var profileImageURL:String, var status : String, var Email : String, var activeState : String ,var token : String?) : Parcelable{
    constructor() : this("","","","","","offline","")

    companion object{
        private val addition = Addition()
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view : CircleImageView , imageUrl : String?){
            imageUrl?.let {
                addition.picassoUseIt(imageUrl,view)
            }
        }


    }
}