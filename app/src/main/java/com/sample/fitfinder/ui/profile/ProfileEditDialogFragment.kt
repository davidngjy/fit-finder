package com.sample.fitfinder.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sample.fitfinder.databinding.DialogFragmentProfileEditDialogBinding
import com.sample.fitfinder.ui.profile.viewmodel.ProfileEditViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditDialogFragment : DialogFragment() {
    private val args: ProfileEditDialogFragmentArgs by navArgs()
    private val viewModel: ProfileEditViewModel by viewModels()

    private lateinit var binding: DialogFragmentProfileEditDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentProfileEditDialogBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        viewModel.setEditAction(args.editAction)

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.confirmButton.setOnClickListener {
            viewModel.confirmEdit()
        }

        viewModel.doneUpdate.observe(this, {
            if (it) {
                dismiss()
            }
        })

        return binding.root
    }
}