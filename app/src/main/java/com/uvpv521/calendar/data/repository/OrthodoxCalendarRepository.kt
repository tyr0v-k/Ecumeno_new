package com.uvpv521.calendar.data.repository

import com.uvpv521.calendar.data.models.CalendarDay
import com.uvpv521.calendar.data.models.Holiday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class OrthodoxCalendarRepository {

    private val fixedHolidays = mapOf(
        "0107" to Holiday(
            name = "christmas",
            date = LocalDate.of(2000, 1, 7),
            priority = 1),
        "0114" to Holiday(
            name = "circumcision_of_the_lord",
            date = LocalDate.of(2000, 1, 14),
            priority = 4),
        "0119" to Holiday(
            name = "theophany",
            date = LocalDate.of(2000, 1, 19),
            priority = 1),

        // Февраль
        "0215" to Holiday(
            name = "presentation_of_the_lord",
            date = LocalDate.of(2000, 2, 15),
            priority = 3),

        // Апрель
        "0407" to Holiday(
            name = "annunciation_of_the_most_holy_theotokos",
            date = LocalDate.of(2000, 4, 7),
            priority = 2),

        // Май
        "0509" to Holiday(
            name = "memorial_day_for_fallen_soldiers",
            date = LocalDate.of(2000, 5, 9),
            priority = 6),

        // Июль
        "0707" to Holiday(
            name = "nativity_of_john_the_baptist",
            date = LocalDate.of(2000, 7, 7),
            priority = 1),

        "0712" to Holiday(
            name = "feast_of_saints_peter_and_paul",
            date = LocalDate.of(2000, 7, 12),
            priority = 1),

        // Август
        "0819" to Holiday(
            name = "transfiguration_of_the_lord",
            date = LocalDate.of(2000, 8, 19),
            priority = 2),
        "0828" to Holiday(
            name = "dormition_of_the_most_holy_theotokos",
            date = LocalDate.of(2000, 8, 28),
            priority = 1),

        // Сентябрь
        "0911" to Holiday(
            name = "beheading_of_john_the_baptist",
            date = LocalDate.of(2000, 9, 11),
            priority = 1),

        "0921" to Holiday(
            name = "nativity_of_the_most_holy_theotokos",
            date = LocalDate.of(2000, 9, 21),
            priority = 1),

        "0927" to Holiday(
            name = "exaltation_of_the_holy_cross",
            date = LocalDate.of(2000, 9, 27),
            priority = 1),

        // Октябрь
        "1014" to Holiday(
            name = "protection_of_the_most_holy_theotokos",
            date = LocalDate.of(2000, 10, 14),
            priority = 2),

        // Декабрь
        "1204" to Holiday(
            name = "entrance_of_the_most_holy_theotokos_into_the_temple",
            date = LocalDate.of(2000, 12, 4),
            priority = 1),
    )

    suspend fun getMonthCalendar(year: Int, month: Int): List<CalendarDay> = withContext(Dispatchers.IO) {
        val days = mutableListOf<CalendarDay>()
        val today = LocalDate.now()

        // Вычисляем Пасху для года
        val easterDate = EasterCalculator.calculateOrthodoxEaster(year)
        val movableHolidays = EasterCalculator.calculateMovableHolidays(easterDate, year)

        // Получаем первый и последний день месяца
        val firstDay = LocalDate.of(year, month, 1)
        val lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth())

        // Добавляем дни предыдущего месяца для заполнения первой недели
        var currentDate = firstDay.with(DayOfWeek.MONDAY)
        if (firstDay.dayOfWeek != DayOfWeek.MONDAY) {
            currentDate = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        }

        val times = if((firstDay.dayOfWeek == DayOfWeek.SUNDAY && lastDay.dayOfMonth >= 30) || (firstDay.dayOfWeek == DayOfWeek.SATURDAY && lastDay.dayOfMonth == 31)){
            42
        } else{
            35
        }
        // Создаем 42 дня (6 недель) для календаря
        repeat(times) {
            val dayHolidays = mutableListOf<Holiday>()

            // Проверяем неподвижные праздники
            val monthDay = String.format("%02d%02d", currentDate.monthValue, currentDate.dayOfMonth)
            fixedHolidays[monthDay]?.let { fixedHoliday ->
                val holiday = fixedHoliday.copy(
                    date = LocalDate.of(year, currentDate.monthValue, currentDate.dayOfMonth)
                )
                dayHolidays.add(holiday)
            }

            // Проверяем подвижные праздники
            movableHolidays.forEach { (name, date) ->
                if (date.year == currentDate.year &&
                    date.monthValue == currentDate.monthValue &&
                    date.dayOfMonth == currentDate.dayOfMonth) {
                    dayHolidays.add(
                        Holiday(
                            name = name,
                            date = currentDate,
                            priority = 2)
                    )
                }
            }

            // Добавляем Пасху
            if (currentDate == easterDate) {
                dayHolidays.add(
                    Holiday(
                        name = "easter",
                        date = currentDate,
                        priority = 0)
                )
            }

            val fastLevel = EasterCalculator.getFastLevel(currentDate, easterDate)

            days.add(
                CalendarDay(
                    date = currentDate,
                    holidays = dayHolidays,
                    fastLevel = fastLevel,
                    isToday = currentDate == today,
                    isSelected = false
                )
            )

            currentDate = currentDate.plusDays(1)
        }

        return@withContext days
    }

    suspend fun getDailyCalendar(): CalendarDay = withContext(Dispatchers.IO) {
        var currentDate = LocalDate.now()
        // Вычисляем Пасху для года
        val easterDate = EasterCalculator.calculateOrthodoxEaster(currentDate.year)
        val movableHolidays = EasterCalculator.calculateMovableHolidays(easterDate, currentDate.year)


        val dayHolidays = mutableListOf<Holiday>()

        // Проверяем неподвижные праздники
        val monthDay = String.format("%02d%02d", currentDate.monthValue, currentDate.dayOfMonth)
        fixedHolidays[monthDay]?.let { fixedHoliday ->
            val holiday = fixedHoliday.copy(
                date = LocalDate.of(currentDate.year, currentDate.monthValue, currentDate.dayOfMonth)
            )
            dayHolidays.add(holiday)
        }

        // Проверяем подвижные праздники
        movableHolidays.forEach { (name, date) ->
            if (date.year == currentDate.year &&
                date.monthValue == currentDate.monthValue &&
                date.dayOfMonth == currentDate.dayOfMonth) {
                dayHolidays.add(
                    Holiday(
                        name = name,
                        date = currentDate,
                        priority = 2)
                )
            }
        }

        // Добавляем Пасху
        if (currentDate == easterDate) {
            dayHolidays.add(
                Holiday(
                    name = "easter",
                    date = currentDate,
                    priority = 0)
            )
        }

        val fastLevel = EasterCalculator.getFastLevel(currentDate, easterDate)

        return@withContext CalendarDay(
            date = currentDate,
            holidays = dayHolidays,
            fastLevel = fastLevel,
            isToday = true,
            isSelected = false
        )
    }
}