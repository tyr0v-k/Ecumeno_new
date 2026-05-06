package com.ecumeno.core.utils.models

import com.ecumeno.core.utils.models.enums.FastLevel
import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val holidays: List<Holiday>,
    val fastLevel: FastLevel,
    val isToday: Boolean = false,
    val currentMonth: Boolean = true
)