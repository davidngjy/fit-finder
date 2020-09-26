package com.sample.fitfinder

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.sample.fitfinder.data.gateway.UserGateway
import com.sample.fitfinder.databinding.ActivityLoginBinding
import com.sample.fitfinder.proto.ConnectUserResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
@FlowPreview
@ExperimentalCoroutinesApi
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()

    @Inject lateinit var googleSignInClient: GoogleSignInClient
    @Inject lateinit var userGateway: UserGateway

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupSignInButton()

        googleSignInClient.silentSignIn().addOnCompleteListener {
            handleSignInResult(it)
        }

        viewModel.status.observe(this, {
            it?.let {
                if (it == ConnectUserResponse.Status.Connected) navigateToMainActivity()
                if (it == ConnectUserResponse.Status.Failed) displayConnectionError()
            }
        })
    }

    private fun setupSignInButton() {
        binding.signInButton.apply {
            setSize(SignInButton.SIZE_WIDE)
            setOnClickListener {
                viewModel.clearErrorMessage()
                binding.signInButton.visibility = View.INVISIBLE
                binding.progressBarCard.visibility = View.VISIBLE
                startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN && resultCode == -1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            viewModel.connectUser(account!!.idToken!!)
        } catch (e: ApiException) {
            when (e.statusCode) {
                GoogleSignInStatusCodes.NETWORK_ERROR -> displayConnectionError()
                else -> Timber.d("Error logging in statusCode: ${e.statusCode}")
            }
        }
    }

    private fun displayConnectionError() {
        showLoginButton()
        viewModel.setErrorMessage(getString(R.string.connection_error))
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoginButton() {
        binding.signInButton.visibility = View.VISIBLE
        binding.progressBarCard.visibility = View.INVISIBLE
    }

    companion object {
        const val RC_SIGN_IN = 1234
    }
}