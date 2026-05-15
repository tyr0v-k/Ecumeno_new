package com.ecumeno.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecumeno.core.calculator.EasterCalculator
import com.ecumeno.core.domain.CalendarDay
import com.ecumeno.core.domain.Confession
import com.ecumeno.data.local.preferences.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        viewModelScope.launch {
            preferencesRepository.confession.collect { confession ->
                loadMonth(uiState.value.currentMonth.year, uiState.value.currentMonth.monthValue)
                _uiState.value = _uiState.value.copy(selectedDate = null)
            }
        }
    }

    fun loadMonth(year: Int, month: Int) {
        _uiState.value = _uiState.value.copy(
            currentMonth = YearMonth.of(year, month),
            calendarDays = EasterCalculator.getMonthCalendar(year, month, Confession.fromPreferences(preferencesRepository.confession.value)),
            hasFast = Confession.fromPreferences(preferencesRepository.confession.value) != Confession.lut
        )
    }

    fun selectDate(date: CalendarDay) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun navigateToPreviousMonth() {
        val previousMonth = uiState.value.currentMonth.minusMonths(1)
        loadMonth(previousMonth.year, previousMonth.monthValue)
    }

    fun navigateToNextMonth() {
        val nextMonth = uiState.value.currentMonth.plusMonths(1)
        loadMonth(nextMonth.year, nextMonth.monthValue)
    }

    fun navigateToToday() {
        val today = LocalDate.now()
        loadMonth(today.year, today.monthValue)
    }
}