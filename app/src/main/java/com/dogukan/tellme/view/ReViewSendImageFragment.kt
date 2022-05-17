package com.dogukan.tellme.view

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dogukan.tellme.databinding.FragmentReViewSendImageBinding
import com.dogukan.tellme.viewmodel.ChatViewModel

class ReViewSendImageFragment : Fragment() {
    private lateinit var binding : FragmentReViewSendImageBinding
    private val viewModel : ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReViewSendImageBinding.inflate(layoutInflater)
        arguments.let { it ->
            val uriString = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).imageURL }
            val toID = it?.let { it1 -> ReViewSendImageFragmentArgs.fromBundle(it1).toID }

            if (uriString!=null){
                val uri = Uri.parse(uriString)
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
                val bitmapDrawable = BitmapDrawable(bitmap)
                binding.sendImageView.setImageDrawable(bitmapDrawable)
                toID?.let { it1 -> sendImageClick(it1,uri) }
            }
        }

        return binding.root
    }
    private fun sendImageClick(toID : String ,uri : Uri){
        binding.SendBtn.setOnClickListener {
            viewModel.sendImage(true)
            viewModel.performSendImage(uri, toID)
            parentFragmentManager.executePendingTransactions()
            parentFragmentManager.popBackStackImmediate()

        }
    }
}