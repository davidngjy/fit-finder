package com.sample.fitfinder.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sample.fitfinder.LoginActivity
import com.sample.fitfinder.databinding.FragmentProfileBinding
import com.sample.fitfinder.ui.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.logOutSuccessful.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    navigateToLogin()
                }
            }
        })

        binding.signOutButton.setOnClickListener {
            viewModel.logOut()
        }

        binding.editEmailButton.setOnClickListener {
            findNavController()
                .navigate(ProfileFragmentDirections.actionProfileFragmentToProfileEditDialogFragment(EditAction.Email))
        }

        binding.editNameButton.setOnClickListener {
            findNavController()
                .navigate(ProfileFragmentDirections.actionProfileFragmentToProfileEditDialogFragment(EditAction.DisplayName))
        }

        return binding.root
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}