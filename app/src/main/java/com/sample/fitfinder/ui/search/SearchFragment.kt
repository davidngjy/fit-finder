package com.sample.fitfinder.ui.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.app.ActivityCompat.getColor
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.sample.fitfinder.R
import com.sample.fitfinder.databinding.FragmentSearchBinding
import com.sample.fitfinder.domain.Session
import com.sample.fitfinder.ui.configureDayNightStyle
import com.sample.fitfinder.ui.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
@ExperimentalTime
class SearchFragment : Fragment(),
    OnRequestPermissionsResultCallback, OnMapReadyCallback {

    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var binding: FragmentSearchBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var converter: Gson

    private var permissionDenied = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        
        converter = Gson()

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.configureDayNightStyle(requireContext())
        map.setInfoWindowAdapter(CustomInfoWindowAdapter(layoutInflater, converter))

        map.setOnInfoWindowClickListener {
            val sessionId = it.tag as Long
            findNavController().navigate(SearchFragmentDirections
                .actionSearchFragmentToSessionDetailFragment(sessionId))
        }

        googleMap.setOnMyLocationButtonClickListener {
            Toast.makeText(requireContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show()
            // Return false so that we don't consume the event and the default behavior still occurs
            // (the camera animates to the user's current position).
            false
        }
        googleMap.setOnMyLocationClickListener { location ->
            Toast.makeText(requireContext(), "Current location:\n$location", Toast.LENGTH_LONG).show()
        }
        googleMap.uiSettings.isMapToolbarEnabled = false

        enableMyLocation()

        searchViewModel.sessions.observe(viewLifecycleOwner) { sessions ->
            map.clear()
            sessions.forEach { session ->
                markSessionOnMap(session)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (grantResults.isNotEmpty() &&
            grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Location Permission Denied")
            .setMessage("Location is required to search nearby trainers.")
            .setPositiveButton("Dismiss") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true

            // Get current location and move the camera
            fusedLocationClient.requestLocationUpdates(LocationRequest.create().setNumUpdates(1), object: LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return

                    val lastLocation = locationResult.lastLocation

                    val cameraUpdate  = CameraUpdateFactory
                        .newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), 14F)
                    map.animateCamera(cameraUpdate)
                }
            }, Looper.getMainLooper())
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun markSessionOnMap(session: Session) {
        val data = MapInfoData(session.id, session.title, session.description, session.sessionDateTime.toEpochMilli())
        
        map.addMarker(
            MarkerOptions()
                .position(session.locationCoordinate)
                .title(converter.toJson(data))
        )

        map.addCircle(
            CircleOptions()
                .center(session.locationCoordinate)
                .radius(1000.0)
                .fillColor(getColor(requireContext(), R.color.markerFillColor))
                .strokeColor(getColor(requireContext(), R.color.markerStrokeColor))
                .strokeWidth(1F)
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}