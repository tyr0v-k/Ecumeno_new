package com.uvpv521.calendar.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvpv521.calendar.data.local.preferences.PrefsHelper
import com.uvpv521.calendar.data.models.CalendarDay
import com.uvpv521.calendar.data.util.EasterCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(prefs: PrefsHelper) : ViewModel() {
    private val calculator = EasterCalculator
    private val confession : String = prefs.confession

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    private val _calendarDays = MutableStateFlow<List<CalendarDay>>(emptyList())
    val calendarDays: StateFlow<List<CalendarDay>> = _calendarDays

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    init {
        loadMonth(currentMonth.value.year, currentMonth.value.monthValue, confession)
    }

    fun loadMonth(year: Int, month: Int, confession: String) {
        viewModelScope.launch {
            _calendarDays.value = calculator.getMonthCalendar(year, month, confession)
            _currentMonth.value = YearMonth.of(year, month)
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun navigateToPreviousMonth() {
        val previousMonth = currentMonth.value.minusMonths(1)
        loadMonth(previousMonth.year, previousMonth.monthValue, confession)
    }

    fun navigateToNextMonth() {
        val nextMonth = currentMonth.value.plusMonths(1)
        loadMonth(nextMonth.year, nextMonth.monthValue, confession)
    }

    fun navigateToToday() {
        val today = LocalDate.now()
        loadMonth(today.year, today.monthValue, confession)
    }
}