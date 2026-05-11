package com.ecumeno.ui.reading.reader

data class ReaderUiState(
    val isBible: Boolean = false,
    val chapters: List<String> = emptyList(),
    val selectedChapterPosition: Int = 0,
    val content: String = "",
    val fontSize: Float = 16f,
    val isMinusEnabled: Boolean = true,
    val isPlusEnabled: Boolean = true,
    val dataCleared: Boolean = false
)