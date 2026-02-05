//package com.uvpv521.calendar.data.database
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//import com.uvpv521.calendar.data.models.Holiday
//import java.time.LocalDate
//
//@Dao
//interface HolidayDao {
//
//    @Query("SELECT * FROM holidays WHERE date = :date")
//    suspend fun getHolidaysByDate(date: LocalDate): List<Holiday>
//
//    @Query("SELECT * FROM holidays WHERE is_movable = 0 AND " +
//            "(strftime('%m', date) = :month AND strftime('%d', date) = :day)")
//    suspend fun getFixedHolidaysByMonthAndDay(month: String, day: String): List<Holiday>
//
//    @Query("SELECT * FROM holidays WHERE date BETWEEN :startDate AND :endDate")
//    suspend fun getHolidaysBetweenDates(startDate: LocalDate, endDate: LocalDate): List<Holiday>
//
//    @Insert
//    suspend fun insertAll(holidays: List<Holiday>)
//
//    @Query("DELETE FROM holidays")
//    suspend fun deleteAll()
//
//    @Query("SELECT COUNT(*) FROM holidays")
//    suspend fun getCount(): Int
//}