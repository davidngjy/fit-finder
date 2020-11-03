package com.sample.fitfinder.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sample.fitfinder.LoginActivity
import com.sample.fitfinder.databinding.FragmentProfileBinding
import com.sample.fitfinder.ui.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.io.File
import java.io.IOException


@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var binding: FragmentProfileBinding
    private lateinit var currentPhotoPath: String

    private var permissionDenied = false

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
                .navigate(
                    ProfileFragmentDirections.actionProfileFragmentToProfileEditDialogFragment(EditAction.Email)
                )
        }

        binding.editNameButton.setOnClickListener {
            findNavController()
                .navigate(
                    ProfileFragmentDirections.actionProfileFragmentToProfileEditDialogFragment(EditAction.DisplayName)
                )
        }

        binding.takeNewPhoto.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Update Profile Picture")
                .setItems(arrayOf("Take from camera", "Choose from gallery")) { dialog, which ->
                    when(which) {
                        0 -> {
                            dispatchTakePictureIntent()
                            dialog.dismiss()
                        }
                        1 -> {
                            chooseFromGallery()
                        }
                    }
                }
                .show()
        }

        return binding.root
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.android.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File {
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "profile_picture", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun chooseFromGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
        }
        else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != REQUEST_EXTERNAL_STORAGE_PERMISSION) return

        if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED)
            chooseFromGallery()
        else
            permissionDenied = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)

            val ei = ExifInterface(currentPhotoPath)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
                else -> bitmap
            }

            viewModel.updateProfilePicture(rotatedBitmap)

            val file = File(currentPhotoPath)
            file.delete()
        }

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, data!!.data!!)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, data!!.data!!)
            }

            viewModel.updateProfilePicture(bitmap)
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()
        if (permissionDenied) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Storage Permission Denied")
                .setMessage("Storage permission is required for choosing an image from gallery")
                .setPositiveButton("Dismiss") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            permissionDenied = false
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 101
        private const val REQUEST_IMAGE_GALLERY = 102
        private const val REQUEST_EXTERNAL_STORAGE_PERMISSION = 103
    }
}