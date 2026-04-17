package com.uvpv521.calendar.data.models

import android.os.Parcelable
//import com.uvpv521.calendar.data.database.Converters
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class Holiday(
    val name: String,
    val date: LocalDate, // Для неподвижных праздников
    val priority: Int, // 1-6 по уставу
) : Parcelable