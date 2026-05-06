package com.ecumeno.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecumeno.data.local.preferences.PrefsHelper
import com.ecumeno.core.utils.EasterCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(private val prefs: PrefsHelper) : ViewModel() {
    private val calculator = EasterCalculator
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState


    init {
        loadMonth(uiState.value.currentMonth.year, uiState.value.currentMonth.monthValue)
    }

    fun loadMonth(year: Int, month: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(currentMonth = YearMonth.of(year, month),
                calendarDays = calculator.getMonthCalendar(year, month, prefs.confession),
                hasFast = prefs.confession != "lut"
            )
        }
    }

    fun selectDate(date: LocalDate) {
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