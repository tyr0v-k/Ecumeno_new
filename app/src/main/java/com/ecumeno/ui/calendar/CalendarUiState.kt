package com.ecumeno.ui.calendar

import com.ecumeno.core.utils.models.CalendarDay
import java.time.YearMonth

data class CalendarUiState (
    val currentMonth: YearMonth = YearMonth.now(),
    val calendarDays: List<CalendarDay> = emptyList(),
    val selectedDate: CalendarDay? = null,
    val hasFast: Boolean = true
)