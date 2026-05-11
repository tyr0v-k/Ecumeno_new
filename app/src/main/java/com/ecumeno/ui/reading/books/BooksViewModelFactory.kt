package com.ecumeno.ui.reading.books

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ecumeno.data.local.preferences.PreferencesRepository

class BooksViewModelFactory(
    private val application: Application,
    private val preferencesRepository: PreferencesRepository,
    private val dbName: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksViewModel::class.java)) {
            return BooksViewModel(
                application,
                preferencesRepository,
                dbName
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}