package com.ecumeno.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ecumeno.data.local.preferences.PreferencesRepository
import com.ecumeno.ui.calendar.CalendarViewModel
import com.ecumeno.ui.rosary.RosaryViewModel

class ViewModelFactory(
    private val preferencesRepository: PreferencesRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                CalendarViewModel(preferencesRepository) as T
            }
            modelClass.isAssignableFrom(RosaryViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                RosaryViewModel(preferencesRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}