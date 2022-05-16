package com.dogukan.tellme

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Debug
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.dogukan.tellme.databinding.FragmentReViewSendImageBinding
import com.dogukan.tellme.view.ChatLogFragmentArgs
import com.dogukan.tellme.view.ChatLogFragmentDirections
import com.dogukan.tellme.viewmodel.ChatViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ReViewSendImageFragment : Fragment() {
    private lateinit var binding : FragmentReViewSendImageBinding
    private val viewModel : ChatViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReViewSendImageBinding.inflate(layoutInflater)

        // Inflate the layout for this fragment
        arguments.let { it ->
            val uriString = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).imageURL }
            val toID = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).toID }

            if (uriString!=null){
                val uri = Uri.parse(uriString)
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver,uri)
                val bitmapDrawable = BitmapDrawable(bitmap)
                binding.sendImageView.setImageDrawable(bitmapDrawable)
                binding.SendBtn.setOnClickListener {
                    viewModel.sendImage(true)
                    toID?.let { it1 -> viewModel.performSendImage(uri, it1) }
                    parentFragmentManager.popBackStack()

                }
            }


        }

        return binding.root
    }
}