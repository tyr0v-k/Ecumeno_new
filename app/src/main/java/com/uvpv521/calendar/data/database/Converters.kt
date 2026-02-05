//package com.uvpv521.calendar.data.database
//
//import androidx.room.TypeConverter
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//
//object Converters {
//    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
//
//    @TypeConverter
//    fun fromLocalDate(date: LocalDate?): String? {
//        return date?.format(formatter)
//    }
//
//    @TypeConverter
//    fun toLocalDate(dateString: String?): LocalDate? {
//        return dateString?.let {
//            try {
//                LocalDate.parse(it, formatter)
//            } catch (e: Exception) {
//                null
//            }
//        }
//    }
//}