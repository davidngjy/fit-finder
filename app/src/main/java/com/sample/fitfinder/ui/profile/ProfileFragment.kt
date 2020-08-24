package com.sample.fitfinder.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sample.fitfinder.data.repository.UserRepository
import com.sample.fitfinder.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)

        val profileViewModelFactory = ProfileViewModelFactory(UserRepository())
        profileViewModel = ViewModelProvider(this, profileViewModelFactory)
            .get(ProfileViewModel::class.java)

        binding.profileViewModel = profileViewModel

        return binding.root
    }
}