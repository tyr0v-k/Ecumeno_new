package com.uvpv521.calendar.data.repository

import com.uvpv521.calendar.data.models.CalendarDay
import com.uvpv521.calendar.data.models.Holiday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class OrthodoxCalendarRepository {

    private val fixedHolidays = mapOf(
        "0107" to Holiday(
            id = "christmas",
            name = "Рождество Христово",
//            description = "Рождество Господа Бога и Спаса нашего Иисуса Христа",
            date = LocalDate.of(2000, 1, 7),
            priority = 1,
            isMovable = false
        ),
        "0114" to Holiday(
            id = "circumcision",
            name = "Обрезание Господне",
//            description = "Память обрезания Иисуса Христа",
            date = LocalDate.of(2000, 1, 14),
            priority = 4,
            isMovable = false
        ),
        "0119" to Holiday(
            id = "baptism",
            name = "Крещение Господне",
//            description = "Богоявление. Крещение Господа Бога и Спаса нашего Иисуса Христа",
            date = LocalDate.of(2000, 1, 19),
            priority = 1,
            isMovable = false
        ),

        // Февраль
        "0215" to Holiday(
            id = "meeting",
            name = "Сретение Господне",
//            description = "Встреча Господа в храме",
            date = LocalDate.of(2000, 2, 15),
            priority = 3,
            isMovable = false
        ),

        // Апрель
        "0407" to Holiday(
            id = "annunciation",
            name = "Благовещение Пресвятой Богородицы",
//            description = "Возвещение архангелом Гавриилом Деве Марии о рождении Иисуса Христа",
            date = LocalDate.of(2000, 4, 7),
            priority = 2,
            isMovable = false
        ),


        // Июль

        // Август
        "0819" to Holiday(
            id = "transfiguration",
            name = "Преображение Господне",
//            description = "Преображение Господа Бога и Спаса нашего Иисуса Христа",
            date = LocalDate.of(2000, 8, 19),
            priority = 2,
            isMovable = false
        ),
        "0828" to Holiday(
            id = "dormition",
            name = "Успение Пресвятой Богородицы",
//            description = "Успение Пресвятой Владычицы нашей Богородицы и Приснодевы Марии",
            date = LocalDate.of(2000, 8, 28),
            priority = 1,
            isMovable = false
        ),

        // Сентябрь
        "0921" to Holiday(
            id = "nativity_of_theotokos",
            name = "Рождество Пресвятой Богородицы",
//            description = "Рождество Пресвятой Владычицы нашей Богородицы и Приснодевы Марии",
            date = LocalDate.of(2000, 9, 21),
            priority = 1,
            isMovable = false
        ),

        // Октябрь
        "1014" to Holiday(
            id = "protection",
            name = "Покров Пресвятой Богородицы",
//            description = "Покров Пресвятой Владычицы нашей Богородицы и Приснодевы Марии",
            date = LocalDate.of(2000, 10, 14),
            priority = 2,
            isMovable = false
        ),

        // Декабрь
        "1204" to Holiday(
            id = "introduction",
            name = "Введение во храм Пресвятой Богородицы",
//            description = "Введение во храм Пресвятой Владычицы нашей Богородицы и Приснодевы Марии",
            date = LocalDate.of(2000, 12, 4),
            priority = 1,
            isMovable = false
        ),
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
                            id = name.lowercase().replace(" ", "_"),
                            name = name,
//                            description = "Подвижный праздник",
                            date = currentDate,
                            priority = 2,
                            isMovable = true
                        )
                    )
                }
            }

            // Добавляем Пасху
            if (currentDate == easterDate) {
                dayHolidays.add(
                    Holiday(
                        id = "easter",
                        name = "Светлое Христово Воскресение. Пасха",
//                        description = "Воскресение Господа Бога и Спаса нашего Иисуса Христа",
                        date = currentDate,
                        priority = 0, // Высший приоритет
                        isMovable = true
                    )
                )
            }

            val fastLevel = EasterCalculator.getFastLevel(currentDate, easterDate)

            days.add(
                CalendarDay(
                    date = currentDate,
                    dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")),
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

//    fun getHolidayDescription(holiday: Holiday): String {
//        return when (holiday.id) {
//            "easter" -> "Пасха - самый главный христианский праздник. " +
//                    "Установлен в честь Воскресения Иисуса Христа."
//            "christmas" -> "Рождество Христово - один из главных христианских праздников. " +
//                    "Установлен в честь рождения Иисуса Христа."
//            "baptism" -> "Крещение Господне - праздник в честь крещения Иисуса Христа " +
//                    "в реке Иордан Иоанном Крестителем."
//            else -> holiday.description
//        }
//    }
//
//    private fun getRussianDayOfWeek(date: LocalDate): String {
//        return when (date.dayOfWeek) {
//            DayOfWeek.MONDAY -> "Пн"
//            DayOfWeek.TUESDAY -> "Вт"
//            DayOfWeek.WEDNESDAY -> "Ср"
//            DayOfWeek.THURSDAY -> "Чт"
//            DayOfWeek.FRIDAY -> "Пт"
//            DayOfWeek.SATURDAY -> "Сб"
//            DayOfWeek.SUNDAY -> "Вс"
//        }
//    }
}