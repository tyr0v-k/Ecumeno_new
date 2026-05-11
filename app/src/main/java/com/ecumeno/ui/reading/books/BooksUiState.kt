package com.ecumeno.ui.reading.books

data class BooksUiState(
    val items: List<String> = emptyList(),
    val itemNumbers: List<Int> = emptyList(),
    val isBible: Boolean = false,
    val dbName: String = "",
    val itemNumber: Int = -1
)