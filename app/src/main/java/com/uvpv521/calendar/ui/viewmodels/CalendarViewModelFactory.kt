package com.uvpv521.calendar.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uvpv521.calendar.data.local.preferences.PrefsHelper

class CalendarViewModelFactory(
    private val prefs: PrefsHelper,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}