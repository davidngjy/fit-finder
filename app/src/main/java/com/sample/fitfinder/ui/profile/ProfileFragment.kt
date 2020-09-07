package com.sample.fitfinder.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sample.fitfinder.data.repository.UserRepository
import com.sample.fitfinder.databinding.FragmentProfileBinding
import com.sample.fitfinder.ui.profile.viewmodel.ProfileViewModel
import com.sample.fitfinder.ui.profile.viewmodel.ProfileViewModelFactory

class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels { ProfileViewModelFactory(UserRepository()) }
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.profileViewModel = profileViewModel

        return binding.root
    }
}