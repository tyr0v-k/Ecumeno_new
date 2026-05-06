package com.ecumeno.ui.rosary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ecumeno.data.local.preferences.PrefsHelper

class RosaryViewModelFactory(private val prefs: PrefsHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RosaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RosaryViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}