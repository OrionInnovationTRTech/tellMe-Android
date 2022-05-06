package com.dogukan.tellme.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.dogukan.tellme.R
import com.dogukan.tellme.databinding.FragmentSettingsBinding
import com.dogukan.tellme.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {
    private lateinit var binding : FragmentSettingsBinding
    private val viewModel : SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.chat ->{
                    val action = SettingsFragmentDirections.actionSettingsFragmentToLatestMessagesFragment2()
                    view.let { it1 -> Navigation.findNavController(it1).navigate(action) }

                    return@setOnItemSelectedListener true
                }
                R.id.settings ->{

                    return@setOnItemSelectedListener true
                }

            }

            return@setOnItemSelectedListener true
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings,container,false)

        viewModel.getUser().observe(viewLifecycleOwner, Observer {
            binding.userModel = it
            binding.settingsProgressBar.visibility = View.GONE

        })
        // Inflate the layout for this fragment
        return binding.root
    }



}