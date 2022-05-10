package com.dogukan.tellme

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dogukan.tellme.databinding.FragmentEditSettingsBinding
import com.dogukan.tellme.databinding.FragmentSettingsBinding
import com.dogukan.tellme.view.MainActivity
import com.dogukan.tellme.viewmodel.SettingsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditSettingsFragment(var Which : Int) : BottomSheetDialogFragment() {

    private val viewModel : SettingsViewModel by viewModels()

    private lateinit var binding : FragmentEditSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {

        super.onStart()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditSettingsBinding.inflate(layoutInflater)

        binding.CancelButton.setOnClickListener{
            this.dismiss()
        }

        if (Which==1){
            binding.ChangeTextView.setText("Change Your Name")
            binding.changedNameTV.setText(viewModel.getUser().value?.username)

            binding.SaveButton.setOnClickListener{

                val userName = binding.changedNameTV.text.toString()
                viewModel.changeUserName(userName)
                this.dismiss()
            }
            Log.d("Which","1")
        }
        else if (Which==2){
            binding.ChangeTextView.setText("Change Your Status")
            binding.changedNameTV.setText(viewModel.getUser().value?.status)
            binding.SaveButton.setOnClickListener{
                val status = binding.changedNameTV.text.toString()
                viewModel.changeStatus(status)
                this.dismiss()
            }
            Log.d("Which","2")
        }

        // Inflate the layout for this fragment
        return binding.root
    }



}