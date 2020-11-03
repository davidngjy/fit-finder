package com.sample.fitfinder.ui.profile.viewmodel

import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.sample.fitfinder.data.repository.CurrentUserRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.math.floor
import kotlin.math.sqrt


@FragmentScoped
class ProfileViewModel @ViewModelInject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val currentUserRepository: CurrentUserRepository
) : ViewModel() {

    val currentUser = currentUserRepository.currentUser.asLiveData()

    private val _logOutSuccessful = MutableLiveData<Boolean>()
    val logOutSuccessful: LiveData<Boolean>
        get() = _logOutSuccessful

    fun updateProfilePicture(newProfilePicture: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            val scaledPicture = scaleBitmap(newProfilePicture)
            val stream = ByteArrayOutputStream()
            scaledPicture.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            currentUserRepository.updateProfilePicture(stream.toByteArray())
        }
    }

    fun logOut() {
        viewModelScope.launch {
            currentUserRepository.clearCurrentUser()
            googleSignInClient.signOut()
            _logOutSuccessful.value = true
        }
    }

    private fun scaleBitmap(input: Bitmap): Bitmap {
        val currentWidth = input.width
        val currentHeight = input.height
        val currentPixels = currentWidth * currentHeight
        // Get the amount of max pixels:
        // 1 pixel = 4 bytes (R, G, B, A)
        val maxPixels = ONE_MB_IN_BYTE / 4 // Floored
        if (currentPixels <= maxPixels) {
            // Already correct size:
            return input
        }
        // Scaling factor when maintaining aspect ratio is the square root since x and y have a relation:
        val scaleFactor = sqrt(maxPixels / currentPixels.toDouble())
        val newWidthPx = floor(currentWidth * scaleFactor).toInt()
        val newHeightPx = floor(currentHeight * scaleFactor).toInt()

        return Bitmap.createScaledBitmap(input, newWidthPx, newHeightPx, true)
    }

    companion object {
        private const val ONE_MB_IN_BYTE = 1024 * 1024
    }
}