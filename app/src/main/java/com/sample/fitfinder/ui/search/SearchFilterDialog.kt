package com.sample.fitfinder.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.sample.fitfinder.databinding.DialogSearchFilterBinding
import com.sample.fitfinder.ui.search.viewmodel.SearchFilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

@AndroidEntryPoint
class SearchFilterDialog : DialogFragment() {

    private val viewModel: SearchFilterViewModel by viewModels()

    private lateinit var binding: DialogSearchFilterBinding
    private lateinit var dateRangePicker: MaterialDatePicker<Pair<Long, Long>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSearchFilterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupDateRangePicker()

        binding.duration.addOnChangeListener { slider, _, _ ->
            viewModel.lowerDuration.value = slider.values[0].toInt()
            viewModel.upperDuration.value = slider.values[1].toInt()
        }

        viewModel.maxPrice.observe(viewLifecycleOwner) {
            viewModel.updateMaxPrice()
        }

        viewModel.isOnline.observe(viewLifecycleOwner) {
            viewModel.updateSessionType()
        }

        viewModel.isInPerson.observe(viewLifecycleOwner) {
            viewModel.updateSessionType()
        }

        viewModel.lowerDuration.observe(viewLifecycleOwner) {
            viewModel.updateDuration()
        }

        viewModel.upperDuration.observe(viewLifecycleOwner) {
            viewModel.updateDuration()
        }

        binding.dateRangeField.editText!!.setOnClickListener {
            showDateRangePicker()
        }

        binding.lowerTimeTextField.editText!!.setOnClickListener {
            showTimePicker(LOWER_TIME_SELECTION)
        }

        binding.upperTimeTextField.editText!!.setOnClickListener {
            showTimePicker(UPPER_TIME_SELECTION)
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.duration.values = arrayListOf(
            viewModel.lowerDuration.value!!.toFloat(),
            viewModel.upperDuration.value!!.toFloat()
        )
    }

    private fun setupDateRangePicker() {
        val builder = MaterialDatePicker
            .Builder
            .dateRangePicker()

        val constraintBuilder = CalendarConstraints
            .Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        dateRangePicker = builder.setCalendarConstraints(constraintBuilder).build()
    }

    private fun showTimePicker(tag: String) {
        val timePicker = MaterialTimePicker
            .Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val localTime = LocalTime.of(timePicker.hour, timePicker.minute)
            if (tag == UPPER_TIME_SELECTION) viewModel.updateUpperTime(localTime)
            else viewModel.updateLowerTime(localTime)
        }

        timePicker.show(parentFragmentManager, tag)
    }

    private fun showDateRangePicker() {
        dateRangePicker.show(childFragmentManager, dateRangePicker.toString())
        dateRangePicker.addOnPositiveButtonClickListener {
            viewModel.updateDate(it.first!!, it.second!!)
        }
    }

    companion object {
        const val UPPER_TIME_SELECTION = "UPPER"
        const val LOWER_TIME_SELECTION = "LOWER"
    }
}