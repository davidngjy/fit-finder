package com.sample.fitfinder.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sample.fitfinder.R
import com.sample.fitfinder.databinding.FragmentSessionDetailBinding
import com.sample.fitfinder.ui.configureDayNightStyle
import com.sample.fitfinder.ui.session.viewmodel.SessionDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
@ExperimentalTime
@FlowPreview
@ExperimentalCoroutinesApi
class SessionDetailFragment : Fragment() {
    private val viewModel: SessionDetailViewModel by viewModels()
    private val args: SessionDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentSessionDetailBinding
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionDetailBinding.inflate(inflater, container, false)
        setActionBarTitle()

        binding.mapCardView.visibility = View.GONE

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.small_map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            map.apply {
                uiSettings.isMapToolbarEnabled = false
                uiSettings.isScrollGesturesEnabled = false
                uiSettings.isZoomGesturesEnabled = false
                uiSettings.isRotateGesturesEnabled = false
                configureDayNightStyle(requireContext())
            }

            viewModel.location.observe(viewLifecycleOwner, { setupMapFragment(it) })
        }

        val sessionId = args.sessionId

        viewModel.setSessionId(sessionId)

        binding.editButton.setOnClickListener {
            findNavController().navigate(SessionDetailFragmentDirections
                .actionSessionDetailFragmentToSessionAddFragment(sessionId))
        }

        binding.bookingButton.setOnClickListener {
            viewModel.addBooking(args.sessionId)
        }

        viewModel.navigateOnBooked.observe(viewLifecycleOwner) {
            it?.let {
                if(it)
                    findNavController().popBackStack()
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        return binding.root
    }

    private fun setActionBarTitle() {
        (activity as AppCompatActivity).supportActionBar?.title = "Session Details"
    }

    private fun setupMapFragment(coordinate: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15F))
        map.addMarker(
            MarkerOptions()
                .position(coordinate)
        )

        binding.mapCardView.visibility = View.VISIBLE
    }
}