package com.sample.fitfinder.ui.session

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.sample.fitfinder.R
import com.sample.fitfinder.databinding.FragmentSessionAddBinding
import com.sample.fitfinder.ui.configureDayNightStyle
import com.sample.fitfinder.ui.session.viewmodel.SessionAddViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@AndroidEntryPoint
class SessionAddFragment : Fragment() {

    private val viewModel: SessionAddViewModel by viewModels()
    private val args: SessionDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentSessionAddBinding
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var geoCoder: Geocoder
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionAddBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.small_map) as SupportMapFragment

        mapFragment.getMapAsync {
            map = it
            map.apply {
                uiSettings.isMapToolbarEnabled = false
                uiSettings.isScrollGesturesEnabled = false
                uiSettings.isZoomGesturesEnabled = false
                uiSettings.isRotateGesturesEnabled = false
                configureDayNightStyle(requireContext())
            }

            viewModel.locationCoordinate.observe(viewLifecycleOwner, { coordinate ->
                setCoordinateToMap(coordinate)
            })
        }

        binding.mapCardView.visibility = View.GONE

        geoCoder = Geocoder(requireContext())

        setActionBarTitle()
        setupDatePicker()
        setupBackNavigation()
        checkForEdit()
        showConfirmButton()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.isOnline.observe(viewLifecycleOwner, { isChecked ->
            if (!isChecked && !viewModel.isInPerson.value!!) {
                viewModel.isInPerson.value = true
            }
        })

        viewModel.isInPerson.observe(viewLifecycleOwner, { isChecked ->
            if (!isChecked && !viewModel.isOnline.value!!) {
                viewModel.isOnline.value = true
            }
        })

        binding.dateTextField.editText!!.setOnClickListener {
            binding.dateTextField.error = null
            binding.dateTextField.isErrorEnabled = false
            showMaterialDatePicker()
        }

        binding.timeTextField.editText!!.setOnClickListener {
            binding.timeTextField.error = null
            binding.timeTextField.isErrorEnabled = false
            showMaterialTimePicker()
        }

        binding.locationTextField.editText!!.setOnClickListener{
            binding.locationTextField.error = null
            binding.locationTextField.isErrorEnabled = false
            launchAutoCompleteIntent()
        }

        binding.durationSlider.addOnChangeListener { _, value, _ ->
            viewModel.duration.value = value.toInt()
        }

        binding.titleTextField.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.validateInput(binding.titleTextField)
            }
        }

        binding.descriptionTextField.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.validateInput(binding.descriptionTextField)
            }
        }

        binding.costTextField.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.validateInput(binding.costTextField)
            }
        }

        binding.timeTextField.editText!!.addTextChangedListener {
            viewModel.validateDateTime()
        }

        binding.confirmButton.setOnClickListener { onConfirmButton() }

        viewModel.navigateOnConfirm.observe(viewLifecycleOwner, {
            it?.let {
                if (it) findNavController().popBackStack()
                else showConfirmButton()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        val address = geoCoder.getFromLocation(place.latLng!!.latitude, place.latLng!!.longitude, 1).first()
                        val suburb = address.locality
                        val postcode = address.postalCode
                        val state = address.adminArea
                        val coordinate = LatLng(address.latitude, address.longitude)

                        viewModel.setCoordinate(coordinate)

                        viewModel.locationString.value = getString(
                            R.string.session_suburb,
                            suburb,
                            postcode,
                            state)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                    }
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setCoordinateToMap(coordinate: LatLng) {
        map.clear()
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 13F))
        map.addMarker(
            MarkerOptions()
                .position(coordinate)
        )

        binding.mapCardView.visibility = View.VISIBLE
    }

    private fun setActionBarTitle() {
        if (args.sessionId == 0L) (activity as AppCompatActivity).supportActionBar?.title = "Add New Session"
        else (activity as AppCompatActivity).supportActionBar?.title = "Edit Session"
    }

    private fun showMaterialTimePicker() {
        val timePicker = MaterialTimePicker
            .Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()

        timePicker.show(parentFragmentManager, timePicker.toString())

        timePicker.addOnPositiveButtonClickListener {
            LocalTime.of(timePicker.hour, timePicker.minute)
                .let {
                    viewModel.localTime.value = it
                    viewModel.timeString.value = it.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
                }
        }
    }

    private fun setupDatePicker() {
        val builder = MaterialDatePicker
            .Builder
            .datePicker()
        val constraintBuilder = CalendarConstraints
            .Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        datePicker = builder.setCalendarConstraints(constraintBuilder).build()
    }

    private fun showMaterialDatePicker() {
        datePicker.show(childFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener {
            val zonedDateTime = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
            viewModel.localDate.value = zonedDateTime.toLocalDate()
            viewModel.dateString.value = datePicker.headerText
        }
    }

    private fun launchAutoCompleteIntent() {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setTypeFilter(TypeFilter.GEOCODE)
            .setCountries(listOf("AU"))
            .build(requireContext())

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun onConfirmButton() {
        if (!validateAllInput()) { return }

        if (!viewModel.validateDateTime()) {
            showDateTimeError()
            return
        } else {
            hideDateTimeError()
        }

        showProgress()
        viewModel.saveSession()
    }

    private fun validateAllInput(): Boolean {
        var isValid = true
        if (!viewModel.validateInput(binding.titleTextField)) { isValid = false }
        if (!viewModel.validateInput(binding.descriptionTextField)) { isValid = false }
        if (!viewModel.validateInput(binding.dateTextField)) { isValid = false }
        if (!viewModel.validateInput(binding.timeTextField)) { isValid = false }
        if (!viewModel.validateInput(binding.locationTextField)) { isValid = false }
        if (!viewModel.validateInput(binding.costTextField)) { isValid = false }
        return isValid
    }

    private fun setupBackNavigation() {
        // This is required to set to true for overriding onOptionsItemSelected
        // Else it'll direct to main activity instead
        setHasOptionsMenu(true)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showConfirmation {
                findNavController().popBackStack()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                showConfirmation {
                    findNavController().popBackStack()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmation(confirmCallback: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Discard New Session")
            .setMessage("Your new session will be discarded")
            .setPositiveButton("Discard") { dialog, _ ->
                confirmCallback()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showDateTimeError() {
        binding.timeTextField.let { layout ->
            layout.isErrorEnabled = true
            layout.error = "Time cannot be in the past"
        }
    }

    private fun hideDateTimeError() {
        binding.timeTextField.let { layout ->
            layout.isErrorEnabled = false
            layout.error = null
        }
    }

    private fun checkForEdit() {
        if (args.sessionId > 0) viewModel.loadSessionDetail(args.sessionId)
    }

    private fun showProgress() {
        binding.progressBarCard.visibility = View.VISIBLE
        binding.confirmButton.visibility = View.INVISIBLE
    }

    private fun showConfirmButton() {
        binding.progressBarCard.visibility = View.INVISIBLE
        binding.confirmButton.visibility = View.VISIBLE
    }

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 23487
    }
}