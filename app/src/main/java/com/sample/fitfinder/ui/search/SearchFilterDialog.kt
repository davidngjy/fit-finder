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
import com.sample.fitfinder.databinding.DialogSearchFilterBinding
import com.sample.fitfinder.ui.search.viewmodel.SearchFilterViewModel
import dagger.hilt.android.AndroidEntryPoint

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

//        viewModel.upperDuration.observe(viewLifecycleOwner) {
//            binding.duration.values = arrayListOf(
//                viewModel.lowerDuration.value!!.toFloat(),
//                viewModel.upperDuration.value!!.toFloat())
//        }
//
//        viewModel.lowerDuration.observe(viewLifecycleOwner) {
//            binding.duration.values = arrayListOf(
//                viewModel.lowerDuration.value!!.toFloat(),
//                viewModel.upperDuration.value!!.toFloat())
//        }

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

    private fun showDateRangePicker() {
        dateRangePicker.show(childFragmentManager, dateRangePicker.toString())
        dateRangePicker.addOnPositiveButtonClickListener {

        }
    }
}