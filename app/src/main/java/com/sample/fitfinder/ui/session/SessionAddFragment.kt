package com.sample.fitfinder.ui.session

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
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
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.databinding.FragmentSessionAddBinding
import com.sample.fitfinder.domain.Session
import com.sample.fitfinder.ui.session.viewmodel.SessionAddViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SessionAddFragment : Fragment() {

    private lateinit var viewModel: SessionAddViewModel
    private lateinit var binding: FragmentSessionAddBinding
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var geocoder: Geocoder

    private val sessionRepository = SessionRepository

    private val formatter: SimpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionAddBinding.inflate(inflater)

        geocoder = Geocoder(requireContext())

        setActionBarTitle()
        setupDatePicker()
        setupBackNavigation()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SessionAddViewModel::class.java)

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

        binding.confirmButton.setOnClickListener { onConfirmButton() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)

                        val address = geocoder.getFromLocation(place.latLng!!.latitude, place.latLng!!.longitude, 1).first()
                        val suburb = address.locality
                        val postcode = address.postalCode
                        val state = address.adminArea

                        viewModel.locationCoordinate = LatLng(address.latitude, address.longitude)

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

    private fun setActionBarTitle() {
        (activity as AppCompatActivity).supportActionBar?.title = "Add New Session"
    }

    private fun showMaterialTimePicker() {
        val timePicker = MaterialTimePicker.newInstance().apply {
            setTimeFormat(TimeFormat.CLOCK_12H)
        }
        timePicker.show(parentFragmentManager, timePicker.toString())

        timePicker.setListener {
            viewModel.time.value!!.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            viewModel.time.value!!.set(Calendar.MINUTE, timePicker.minute)

            viewModel.timeString.value = formatter.format(viewModel.time.value!!.time)
        }
    }

    private fun setupDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val constraintBuilder = CalendarConstraints.Builder().apply {
            setValidator(DateValidatorPointForward.now())
        }.build()

        datePicker = builder.setCalendarConstraints(constraintBuilder).build()
    }

    private fun showMaterialDatePicker() {
        datePicker.show(parentFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener {
            viewModel.date.value!!.time = Date(it)
            viewModel.dateString.value = datePicker.headerText
        }
    }

    private fun launchAutoCompleteIntent() {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setTypeFilter(TypeFilter.REGIONS)
            .setCountries(listOf("AU"))
            .build(requireContext())

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun onConfirmButton() {
        if (!validateAllInput()) { return }
        if (!viewModel.validateDateTime(binding.timeTextField)) { return }

        val newSession = Session(
            1,
            1,
            viewModel.title.value!!,
            viewModel.description.value!!,
            viewModel.getConvertedDateTime(),
            viewModel.locationCoordinate,
            viewModel.locationString.value!!,
            viewModel.isOnline.value!!,
            viewModel.isInPerson.value!!,
            viewModel.cost.value!!,
            viewModel.duration.value!!
            )
        sessionRepository.addSession(newSession)

        this.findNavController().navigate(
            SessionAddFragmentDirections.actionSessionAddFragmentToSessionFragment()
        )
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showConfirmation {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                showConfirmation {
                    this.findNavController().navigate(SessionAddFragmentDirections.actionSessionAddFragmentToSessionFragment())
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

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 23487
    }
}