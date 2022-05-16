package com.dogukan.tellme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dogukan.tellme.databinding.FragmentFriendDetailBinding
import com.dogukan.tellme.databinding.FragmentSettingsBinding
import com.dogukan.tellme.models.Users
import com.dogukan.tellme.util.AppUtil
import com.dogukan.tellme.view.ChatLogFragmentArgs
import com.dogukan.tellme.viewmodel.SettingsViewModel

class FriendDetailFragment : Fragment() {

    private lateinit var ToID : String
    private var position : Int = 0
    private lateinit var myName : String
    private lateinit var hisImage : String
    private lateinit var ActiveState : String
    private lateinit var status : String
    private lateinit var email : String
    private lateinit var user : Users
    private val appUtil = AppUtil()
    private val viewModel : SettingsViewModel by viewModels()
    private lateinit var binding : FragmentFriendDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_friend_detail ,container,false)
        arguments?.let {
            position = FriendDetailFragmentArgs.fromBundle(it).position
            ToID = FriendDetailFragmentArgs.fromBundle(it).toID
            myName = FriendDetailFragmentArgs.fromBundle(it).userName
            hisImage = FriendDetailFragmentArgs.fromBundle(it).imageURL
            ActiveState = FriendDetailFragmentArgs.fromBundle(it).activeState
            status = FriendDetailFragmentArgs.fromBundle(it).status
            email = FriendDetailFragmentArgs.fromBundle(it).email
        }
        user = Users(appUtil.getUID()!!,myName,hisImage,status,email, ActiveState,"")
        viewModel.getUser(ToID).observe(viewLifecycleOwner, Observer {
            binding.userModel = it
            binding.settingsProgressBar.visibility = View.GONE

        })

        return binding.root
    }
}