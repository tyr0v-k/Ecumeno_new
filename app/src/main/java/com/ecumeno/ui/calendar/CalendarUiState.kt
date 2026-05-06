package com.ecumeno.ui.calendar

import com.ecumeno.core.utils.models.CalendarDay
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState (
    val currentMonth: YearMonth = YearMonth.now(),
    val calendarDays: List<CalendarDay> = emptyList(),
    val selectedDate: LocalDate? = null,
    val hasFast: Boolean = true
)