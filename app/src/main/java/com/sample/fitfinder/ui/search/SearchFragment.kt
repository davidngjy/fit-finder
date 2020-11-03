package com.sample.fitfinder.ui.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sample.fitfinder.R
import com.sample.fitfinder.databinding.FragmentSearchBinding
import com.sample.fitfinder.domain.Session
import com.sample.fitfinder.ui.configureDayNightStyle
import com.sample.fitfinder.ui.search.viewmodel.SearchViewModel
import com.sample.fitfinder.ui.session.SessionAdapter
import com.sample.fitfinder.ui.session.SessionListItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
@ExperimentalTime
@FlowPreview
@ExperimentalCoroutinesApi
class SearchFragment : Fragment(),
    OnRequestPermissionsResultCallback, OnMapReadyCallback {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var binding: FragmentSearchBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    @Inject lateinit var adapter: SessionAdapter

    private var permissionDenied = false
    private var userDraggedMap = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.listView.adapter = adapter
        binding.listView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        adapter.setClickListener(SessionListItemListener { sessionId ->
            findNavController().navigate(SearchFragmentDirections
                .actionSearchFragmentToSessionDetailFragment(sessionId))
        })

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        binding.myLocationFab.setOnClickListener {
            moveCameraToMyLocation()
        }

        viewModel.availableSessions.observe(viewLifecycleOwner) {
            binding.bottomSheet.visibility = View.VISIBLE
            if (it.isEmpty())
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            it?.let {
                adapter.submitList(it)
            }
        }

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.configureDayNightStyle(requireContext())
        map.setInfoWindowAdapter(CustomInfoWindowAdapter(layoutInflater))

        map.setOnInfoWindowClickListener {
            val sessionId = it.tag as Long
            findNavController().navigate(SearchFragmentDirections
                .actionSearchFragmentToSessionDetailFragment(sessionId))
        }

        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        moveCameraToMyLocation()

        map.setOnCameraMoveStartedListener {
            if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE)
                userDraggedMap = true
        }

        map.setOnCameraIdleListener {
            if (userDraggedMap) {
                viewModel.setBounds(map.projection.visibleRegion.latLngBounds)
                userDraggedMap = false
            }
        }

        viewModel.availableSessions.observe(viewLifecycleOwner) { sessions ->
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
            moveCameraToMyLocation()
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
    private fun moveCameraToMyLocation() {
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
                        .newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), 13.5F)

                    map.animateCamera(cameraUpdate, object: GoogleMap.CancelableCallback {
                        override fun onFinish() {
                            viewModel.setBounds(map.projection.visibleRegion.latLngBounds)
                        }
                        override fun onCancel() { }
                    })
                }
            }, Looper.getMainLooper())
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun markSessionOnMap(session: Session) {
        val data = MapInfoData(session.sessionId,
            session.title,
            session.description,
            session.sessionDateTime.toEpochMilli())
        
        map.addMarker(
            MarkerOptions()
                .position(session.location)
                .title(Json.encodeToString(data))
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}