package com.uvpv521.calendar.data.util

import com.uvpv521.calendar.data.models.CalendarDay
import com.uvpv521.calendar.data.models.enums.FastLevel
import com.uvpv521.calendar.data.models.Holiday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.temporal.TemporalAdjusters
import kotlin.collections.mapOf

object EasterCalculator {

    lateinit var fixedHolidays : Map<String, Holiday>
    /**
     * Вычисление даты Пасхи по старому стилю (григорианский календарь)
     * Алгоритм Гаусса для православной Пасхи
     */
    fun calculateOrthodoxEaster(year: Int): LocalDate {
        val a = year % 19
        val b = year % 4
        val c = year % 7
        val d = (19 * a + 15) % 30
        val e = (2 * b + 4 * c + 6 * d + 6) % 7
        val f = d + e

        // Дата Пасхи по старому стилю
        var day = if (f <= 9) {
            22 + f
        } else {
            f - 9
        }
        var month = if (f <= 9) Month.MARCH else Month.APRIL

        // Переход на григорианский календарь (+13 дней)
        var date = LocalDate.of(year, month, day).plusDays(13)

        // Если дата выходит на май, корректируем
        if (date.month == Month.MAY && date.dayOfMonth > 7) {
            date = date.minusDays(7)
        }

        return date
    }

    fun calculateGregorianEaster(year: Int): LocalDate {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451

        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1

        return LocalDate.of(year, month, day)
    }
    fun getOrthodoxFixedHolidays(){
        fixedHolidays = mapOf(
            "0107" to Holiday(
                name = "christmas",
                priority = 1),
        "0114" to Holiday(
            name = "circumcision_of_the_lord",
            priority = 4),
        "0119" to Holiday(
            name = "theophany",
            priority = 1),

        // Февраль
        "0215" to Holiday(
            name = "presentation_of_the_lord",
            priority = 3),

        // Апрель
        "0407" to Holiday(
            name = "annunciation_of_the_most_holy_theotokos",
            priority = 2),

        // Май
        "0509" to Holiday(
            name = "memorial_day_for_fallen_soldiers",
            priority = 6),

        // Июль
        "0707" to Holiday(
            name = "nativity_of_john_the_baptist",
            priority = 1),

        "0712" to Holiday(
            name = "feast_of_saints_peter_and_paul",
            priority = 1),

        // Август
        "0819" to Holiday(
            name = "transfiguration_of_the_lord",
            priority = 2),
        "0828" to Holiday(
            name = "dormition_of_the_most_holy_theotokos",
            priority = 1),

        // Сентябрь
        "0911" to Holiday(
            name = "beheading_of_john_the_baptist",
            priority = 1),

        "0921" to Holiday(
            name = "nativity_of_the_most_holy_theotokos",
            priority = 1),

        "0927" to Holiday(
            name = "exaltation_of_the_holy_cross",
            priority = 1),

        // Октябрь
        "1014" to Holiday(
            name = "protection_of_the_most_holy_theotokos",
            priority = 2),

        // Декабрь
        "1204" to Holiday(
            name = "entrance_of_the_most_holy_theotokos_into_the_temple",
            priority = 1),
        )
    }

    /**
     * Вычисление всех подвижных праздников на основе даты Пасхи
     */
    fun calculateMovableHolidays(easterDate: LocalDate): Map<String, LocalDate> {
        val holidays = mutableMapOf<String, LocalDate>()

        // Перед Пасхой
        holidays["forgiveness_sunday"] = easterDate.minusDays(49)
        holidays["lazarus_saturday"] = easterDate.minusDays(8)
        holidays["palm_sunday"] = easterDate.minusDays(7)

        // Великий пост
        holidays["triumph_of_orthodoxy"] = easterDate.minusDays(42)
        holidays["good_friday"] = easterDate.minusDays(2)

        // После Пасхи
        holidays["thomas_sunday"] = easterDate.plusDays(7)
        holidays["ascension_of_the_lord"] = easterDate.plusDays(39)
        holidays["pentecost"] = easterDate.plusDays(49)
        holidays["day_of_the_holy_spirit"] = easterDate.plusDays(50)

        // Дни поминовения
        holidays["universal_memorial_saturday"] = easterDate.minusDays(57)
        holidays["lent_saturday_2"] = easterDate.minusDays(36)
        holidays["lent_saturday_3"] = easterDate.minusDays(29)
        holidays["lent_saturday_4"] = easterDate.minusDays(22)
        holidays["lent_saturday_5"] = easterDate.minusDays(15)
        holidays["radonitsa"] = easterDate.plusDays(9)
        holidays["trinity_saturday"] = easterDate.plusDays(48)
        holidays["demetrius_saturday"] = LocalDate.of(easterDate.year, Month.NOVEMBER, 8).minusDays(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY))

        return holidays
    }

    /**
     * Получение уровня поста для даты
     */
    fun getFastLevel(date: LocalDate, easterDate: LocalDate): FastLevel {
        val dayOfWeek = date.dayOfWeek.value

        // Проверяем сплошные седмицы
        if (isContinuousWeek(date, easterDate)) {
            return FastLevel.CONTINUOUS_WEEK
        }

        // Великий пост
        if (isGreatLent(date, easterDate)) {
            return when {
                date.dayOfWeek.value == 7 || date.dayOfWeek.value == 6 -> FastLevel.WINE_OIL_ALLOWED
                date.dayOfWeek.value == 1 || date.dayOfWeek.value == 2 || date.dayOfWeek.value == 4 -> FastLevel.NO_OIL
                else -> FastLevel.XEROPHAGY
            }
        }

        // Рождественский пост
        if (isChristmasFast(date, date.year)) {
            return when {
                date.dayOfWeek.value == 7 || date.dayOfWeek.value == 1 -> FastLevel.NO_FISH
                else -> FastLevel.XEROPHAGY
            }
        }

        // Петров пост
        if (isPeterFast(date, easterDate)) {
            return when {
                date.dayOfWeek.value == 7 || date.dayOfWeek.value == 1 -> FastLevel.NO_FISH
                else -> FastLevel.XEROPHAGY
            }
        }

        // Успенский пост
        if (isDormitionFast(date, date.year)) {
            return FastLevel.FAST
        }

        // Среда и пятница - постные дни (кроме сплошных седмиц)
        if (dayOfWeek == 3 || dayOfWeek == 5) {
            return FastLevel.FAST
        }

        return FastLevel.NO_FAST
    }

    private fun isGreatLent(date: LocalDate, easterDate: LocalDate): Boolean {
        val start = easterDate.minusDays(48)
        val end = easterDate.minusDays(1)
        return !date.isBefore(start) && !date.isAfter(end)
    }

    private fun isContinuousWeek(date: LocalDate, easterDate: LocalDate): Boolean {
        // Святки, Мытаря и фарисея, Сырная, Пасхальная, Троицкая седмицы
        val year = date.year

        // Святки
        val christmastideStart = LocalDate.of(year, 1, 7)
        val christmastideEnd = LocalDate.of(year, 1, 17)

        // Мытаря и фарисея

        val publicanWeekStart = easterDate.minusDays(69)
        val publicanWeekEnd = easterDate.minusDays(63)

        // Cырная

        val cheeseFareWeekStart = easterDate.minusDays(55)
        val cheeseFareWeekEnd = easterDate.minusDays(49)

        // Пасхальная седмица
        val brightWeekStart = easterDate.plusDays(1)
        val brightWeekEnd = easterDate.plusDays(6)

        // Троицкая седмица
        val pentecostWeekStart = easterDate.plusDays(50)
        val pentecostWeekEnd = easterDate.plusDays(56)


        return (!date.isBefore(christmastideStart) && !date.isAfter(christmastideEnd)) ||
                (!date.isBefore(publicanWeekStart) && !date.isAfter(publicanWeekEnd)) ||
                (!date.isBefore(cheeseFareWeekStart) && !date.isAfter(cheeseFareWeekEnd)) ||
                (!date.isBefore(brightWeekStart) && !date.isAfter(brightWeekEnd)) ||
                (!date.isBefore(pentecostWeekStart) && !date.isAfter(pentecostWeekEnd))
    }

    private fun isChristmasFast(date: LocalDate, year: Int): Boolean {
        val start = LocalDate.of(year, 11, 28)
        val end = LocalDate.of(year + 1, 1, 6)
        return (!date.isBefore(start) && !date.isAfter(end)) && date.year == year
    }

    private fun isPeterFast(date: LocalDate, easterDate: LocalDate): Boolean {
        val start = easterDate.plusDays(57)
        val end = LocalDate.of(date.year, 7, 11)
        return !date.isBefore(start) && !date.isAfter(end)
    }

    private fun isDormitionFast(date: LocalDate, year: Int): Boolean {
        val start = LocalDate.of(year, 8, 14)
        val end = LocalDate.of(year, 8, 27)
        return !date.isBefore(start) && !date.isAfter(end)
    }

    suspend fun getMonthCalendar(year: Int, month: Int, confession: String): List<CalendarDay> = withContext(Dispatchers.IO) {
        val days = mutableListOf<CalendarDay>()
        val today = LocalDate.now()
        val easterDate : LocalDate
        getOrthodoxFixedHolidays()
        // Вычисляем Пасху для года
        if(confession == "ort"){
            easterDate = calculateOrthodoxEaster(year)
        } else {
            easterDate = calculateGregorianEaster(year)
        }
        val movableHolidays = calculateMovableHolidays(easterDate)

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
            fixedHolidays[monthDay]?.let { fixedHoliday -> dayHolidays.add(fixedHoliday)
            }

            // Проверяем подвижные праздники
            movableHolidays.forEach { (name, date) ->
                if (date.year == currentDate.year &&
                    date.monthValue == currentDate.monthValue &&
                    date.dayOfMonth == currentDate.dayOfMonth) {
                    dayHolidays.add(
                        Holiday(
                            name = name,
                            priority = 2)
                    )
                }
            }

            // Добавляем Пасху
            if (currentDate == easterDate) {
                dayHolidays.add(
                    Holiday(
                        name = "easter",
                        priority = 0)
                )
            }

            val fastLevel = getFastLevel(currentDate, easterDate)

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

    suspend fun getDailyCalendar(confession: String): CalendarDay = withContext(Dispatchers.IO) {
        val currentDate = LocalDate.now()
        val easterDate : LocalDate
        getOrthodoxFixedHolidays()
        // Вычисляем Пасху для года
        if (confession == "ort"){
            easterDate = calculateOrthodoxEaster(currentDate.year)
        } else{
            easterDate = calculateGregorianEaster(currentDate.year)
        }
        val movableHolidays = calculateMovableHolidays(easterDate)

        val dayHolidays = mutableListOf<Holiday>()

        // Проверяем неподвижные праздники
        val monthDay = String.format("%02d%02d", currentDate.monthValue, currentDate.dayOfMonth)
        fixedHolidays[monthDay]?.let { fixedHoliday -> dayHolidays.add(fixedHoliday)
        }

        // Проверяем подвижные праздники
        movableHolidays.forEach { (name, date) ->
            if (date.year == currentDate.year &&
                date.monthValue == currentDate.monthValue &&
                date.dayOfMonth == currentDate.dayOfMonth) {
                dayHolidays.add(
                    Holiday(
                        name = name,
                        priority = 2)
                )
            }
        }

        // Добавляем Пасху
        if (currentDate == easterDate) {
            dayHolidays.add(
                Holiday(
                    name = "easter",
                    priority = 0)
            )
        }

        val fastLevel = getFastLevel(currentDate, easterDate)

        return@withContext CalendarDay(
            date = currentDate,
            holidays = dayHolidays,
            fastLevel = fastLevel,
            isToday = true,
            isSelected = false
        )
    }
}