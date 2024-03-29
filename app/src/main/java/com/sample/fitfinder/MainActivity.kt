package com.sample.fitfinder

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.libraries.places.api.Places
import com.sample.fitfinder.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
@ExperimentalTime
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        Places.initialize(applicationContext, getString(R.string.google_maps_key))

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupNavControllerDestinationChange(navController)
        setSupportActionBar(binding.toolbar)

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.searchFragment,
            R.id.sessionFragment,
            R.id.messageFragment,
            R.id.profileFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Clear all session in local DB then subscribe to any updates
        viewModel.clear()
        viewModel.subscribe()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setupNavControllerDestinationChange(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in arrayListOf(
                    R.id.searchFragment,
                    R.id.sessionFragment,
                    R.id.messageFragment,
                    R.id.profileFragment,
                    R.id.searchFilterDialog
                )){
                binding.navView.visibility = View.VISIBLE
            } else {
                binding.navView.visibility = View.GONE
            }
        }
    }
}