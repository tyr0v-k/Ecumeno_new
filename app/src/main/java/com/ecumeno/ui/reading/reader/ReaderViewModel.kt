package com.ecumeno.ui.reading.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ecumeno.EcumenoApp
import com.ecumeno.data.local.database.DatabaseHelper
import com.ecumeno.data.local.preferences.Confession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReaderViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesRepository = (application as EcumenoApp).preferencesRepository
    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReaderUiState()
    )
    private var dbHelper: DatabaseHelper? = null
    private var bookNumber: Int = 0
    private var categoryNumber: Int = 0

    fun initialize(number: Int, dbName: String) {
        viewModelScope.launch {
            if (dbName.contains("bible")){
                preferencesRepository.bibleCleared.collect { cleared ->
                    if (cleared){
                        clearReadingProgress()
                        _uiState.value = ReaderUiState(
                            dataCleared = true
                        )
                    } else{
                        val confession = Confession.fromPreferences(preferencesRepository.confession.value)
                        dbHelper = DatabaseHelper(getApplication(), dbName, confession)
                        bookNumber = number
                        initializeBible(number)
                        initializeFontSize()
                    }
                }
            }
            else{
                preferencesRepository.prayersCleared.collect { cleared ->
                    if (cleared){
                        _uiState.value = ReaderUiState(
                            dataCleared = true
                        )
                    } else{
                        val confession = Confession.fromPreferences(preferencesRepository.confession.value)
                        dbHelper = DatabaseHelper(getApplication(), dbName, confession)
                        categoryNumber = number
                        initializePrayers(number)
                        initializeFontSize()
                    }
                }
            }
        }
    }

    private fun initializeBible(number: Int) {
        val chapters = dbHelper?.getChapters(number) ?: return
        val chaptersList = chapters.map { "$it" }
        val lastChapter = preferencesRepository.lastChapter.value
        val initialPosition = if (lastChapter != -1) lastChapter else 0
        _uiState.value = _uiState.value.copy(
            isBible = true,
            chapters = chaptersList,
            selectedChapterPosition = initialPosition
        )
        updateBibleContent(number, chapters[initialPosition])
    }

    private fun initializePrayers(number: Int) {
        val prayers = dbHelper?.getPrayers(number) ?: return
        val prayersList = prayers.map { it.prayerName }
        val firstPrayerContent = prayers.firstOrNull()?.text?.replace("\\n", "\n\n") ?: ""
        _uiState.value = _uiState.value.copy(
            isBible = false,
            chapters = prayersList,
            content = firstPrayerContent
        )
    }

    private fun initializeFontSize() {
        val currentFontSize = preferencesRepository.fontSize.value
        _uiState.value = _uiState.value.copy(
            fontSize = currentFontSize,
            isMinusEnabled = currentFontSize > 10f,
            isPlusEnabled = currentFontSize < 40f
        )
    }

    fun onChapterSelected(position: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.isBible) {
                val number = bookNumber
                val chapters = dbHelper?.getChapters(number) ?: return@launch
                if (position < chapters.size) {
                    preferencesRepository.setLastChapter(position)
                    updateBibleContent(number, chapters[position])
                    _uiState.value = _uiState.value.copy(selectedChapterPosition = position)
                }
            } else {
                val prayers = dbHelper?.getPrayers(categoryNumber) ?: return@launch
                if (position < prayers.size) {
                    _uiState.value = _uiState.value.copy(
                        content = prayers[position].text.replace("\\n", "\n\n"),
                        selectedChapterPosition = position
                    )
                }
            }
        }
    }

    private fun updateBibleContent(number: Int, chapter: Int) {
        val verses = dbHelper?.getVerses(number, chapter) ?: return
        val content = verses.joinToString("\n\n") { verse ->
            "${verse.verse}. ${android.text.Html.fromHtml(
                verse.text.replace(
                    Regex("<[Ss][^>]*>.*?</[Ss]>", RegexOption.IGNORE_CASE),
                    ""
                )
            )}".replace("\\[.*?\\]".toRegex(), "")
        }
        _uiState.value = _uiState.value.copy(content = content)
    }

    fun increaseFontSize() {
        val currentSize = preferencesRepository.fontSize.value
        if (currentSize < 40f) {
            val newSize = currentSize + 2f
            preferencesRepository.setFontSize(newSize)
            updateFontSizeState(newSize)
        }
    }

    fun decreaseFontSize() {
        val currentSize = preferencesRepository.fontSize.value
        if (currentSize > 10f) {
            val newSize = currentSize - 2f
            preferencesRepository.setFontSize(newSize)
            updateFontSizeState(newSize)
        }
    }

    private fun updateFontSizeState(newSize: Float) {
        _uiState.value = _uiState.value.copy(
            fontSize = newSize,
            isMinusEnabled = newSize > 10f,
            isPlusEnabled = newSize < 40f
        )
    }

    fun nextChapter() {
        val currentState = _uiState.value
        if (currentState.selectedChapterPosition < currentState.chapters.size - 1) {
            onChapterSelected(currentState.selectedChapterPosition + 1)
        }
    }

    fun previousChapter() {
        val currentState = _uiState.value
        if (currentState.selectedChapterPosition > 0) {
            onChapterSelected(currentState.selectedChapterPosition - 1)
        }
    }

    fun clearReadingProgress() {
        viewModelScope.launch {
            if (dbHelper?.databaseName?.contains("bible") ?: false){
                preferencesRepository.clearReadingProgress()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dbHelper?.close()
    }
}