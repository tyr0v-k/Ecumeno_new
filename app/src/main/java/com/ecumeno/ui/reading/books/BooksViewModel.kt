package com.ecumeno.ui.reading.books

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ecumeno.EcumenoApp
import com.ecumeno.data.local.database.DatabaseHelper
import com.ecumeno.core.domain.Confession
import com.ecumeno.data.local.preferences.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class BooksViewModel(
    application: Application,
    private val preferencesRepository: PreferencesRepository,
    private val dbName: String
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
        viewModelScope.launch {
            if (dbName.contains("bible")) {
                merge(
                    preferencesRepository.bibleCleared.filter { it },
                    preferencesRepository.confession.map { }
                ).collect {
                    loadBooks()
                }
            } else {
                preferencesRepository.prayersCleared.collect { cleared ->
                    if (cleared) {
                        loadBooks()
                    }
                }
            }
        }
    }

    private fun loadBooks() {
        viewModelScope.launch {
            val context = getApplication<EcumenoApp>().applicationContext
            val confession = Confession.fromPreferences(
                preferencesRepository.confession.value
            )

            val dbHelper = DatabaseHelper(context, dbName, confession)

            if (dbName.contains("bible")) {
                val lastBook = preferencesRepository.lastBook.value
                val books = dbHelper.getBooks()

                if (lastBook != -1 && !preferencesRepository.bibleCleared.value) {
                    _uiState.value = BooksUiState(
                        items = books.map { it.longName },
                        itemNumbers = books.map { it.bookNumber },
                        isBible = true,
                        dbName = dbName,
                        itemNumber = lastBook
                    )
                } else{
                    _uiState.value = BooksUiState(
                        items = books.map { it.longName },
                        itemNumbers = books.map { it.bookNumber },
                        isBible = true
                    )
                }
                preferencesRepository.setBibleCleared(false)
            } else {
                val categories = dbHelper.getCategories()
                _uiState.value = BooksUiState(
                    items = categories.map { it.name },
                    itemNumbers = categories.map { it.categoryNumber },
                    isBible = false
                )
                preferencesRepository.setPrayersCleared(false)
            }
            dbHelper.close()
        }
    }

    fun onItemClicked(position: Int) {
        val currentState = _uiState.value
        val itemNumber = currentState.itemNumbers[position]

        viewModelScope.launch {
            if (currentState.isBible) {
                preferencesRepository.setBibleCleared(false)
                preferencesRepository.setLastBook(itemNumber)
            }
            else{
                preferencesRepository.setPrayersCleared(false)
            }
            _uiState.value = _uiState.value.copy(
                dbName = dbName,
                itemNumber = itemNumber
            )
        }
    }

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(
            itemNumber = -1
        )
    }
}