package com.uvpv521.calendar.data.models

import android.os.Parcelable
//import com.uvpv521.calendar.data.database.Converters
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
//@Entity(tableName = "holidays")
//@TypeConverters(Converters::class)
data class Holiday(
//    @PrimaryKey
    val id: String,
    val name: String,
//    val description: String,
    val date: LocalDate, // Для неподвижных праздников
    val priority: Int, // 1-6 по уставу
    val isMovable: Boolean,
    val easterOffset: Int? = null // Смещение от Пасхи для подвижных праздников
) : Parcelable{
    // Метод для получения строки формата ММ-ДД
    fun getMonthDayString(): String {
        return "${date.monthValue.toString().padStart(2, '0')}-" +
                "${date.dayOfMonth.toString().padStart(2, '0')}"
    }
}


enum class FastLevel {
    NO_FAST,
    CONTINUOUS_WEEK,
    XEROPHAGY, // Сухоядение
    NO_FISH, // Рыба нельзя
    NO_OIL, // Масло нельзя
    FAST, // Строгий пост
    WINE_OIL_ALLOWED // Вино и масло разрешены
}

data class CalendarDay(
    val date: LocalDate,
    val dayOfWeek: String,
    val holidays: List<Holiday>,
    val fastLevel: FastLevel,
    val isToday: Boolean = false,
    val isSelected: Boolean = false
)

