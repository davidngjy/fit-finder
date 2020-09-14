package com.sample.fitfinder.ui

import android.text.TextUtils
import androidx.databinding.InverseMethod

class DataBindingConverter {
    companion object {
        @JvmStatic
        fun convertIntToString(value: Int?): String {
            return value?.toString() ?: ""
        }

        @InverseMethod("convertIntToString")
        @JvmStatic
        fun convertStringToInt(value: String): Int {
            if (TextUtils.isEmpty(value)) {
                return 0
            }
            return value.toInt()
        }
    }
}