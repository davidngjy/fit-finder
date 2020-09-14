package com.sample.fitfinder

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.card.MaterialCardView
import com.sample.fitfinder.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressBarCard: MaterialCardView
    private lateinit var signInButton: SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        progressBarCard = binding.progressBarCard
        signInButton = binding.signInButton
        setupSignInButton()

        googleSignInClient.silentSignIn().addOnCompleteListener {
            handleSignInResult(it)
        }
    }

    private fun setupSignInButton() {
        signInButton.apply {
            setSize(SignInButton.SIZE_WIDE)
            setOnClickListener {
                signInButton.visibility = View.INVISIBLE
                progressBarCard.visibility = View.VISIBLE
                startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        signInButton.visibility = View.VISIBLE
        progressBarCard.visibility = View.INVISIBLE
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

            // TODO(developer): send ID Token to server and validate

            navigateToMainActivity()
        } catch (e: ApiException) {
            if (e.statusCode != 4) binding.error.text = e.statusCode.toString()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val RC_SIGN_IN = 1234
    }
}