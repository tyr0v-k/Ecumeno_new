package com.uvpv521.calendar.data.models

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val holidays: List<Holiday>,
    val fastLevel: FastLevel,
    val isToday: Boolean = false,
    val isSelected: Boolean = false
)