package com.ecumeno.core.utils

import com.ecumeno.core.utils.models.CalendarDay
import com.ecumeno.core.utils.models.enums.FastLevel
import com.ecumeno.core.utils.models.Holiday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.temporal.TemporalAdjusters

object EasterCalculator {

    lateinit var fixedHolidays : MutableMap<String, Holiday>
    // Алгоритм Гаусса для юлианской Пасхи и перевод в григорианский календарь
    fun calculateOrthodoxEaster(year: Int): LocalDate {
        val a = year % 19
        val b = year % 4
        val c = year % 7
        val d = (19 * a + 15) % 30
        val e = (2 * b + 4 * c + 6 * d + 6) % 7
        val f = d + e

        // Дата Пасхи по старому стилю
        val day = if (f <= 9) {
            22 + f
        } else {
            f - 9
        }
        val month = if (f <= 9) Month.MARCH else Month.APRIL

        // Переход на григорианский календарь (+13 дней)
        var date = LocalDate.of(year, month, day).plusDays(13)

        // Если дата выходит на май, корректируем
        if (date.month == Month.MAY && date.dayOfMonth > 7) {
            date = date.minusDays(7)
        }

        return date
    }
    // Функция вычисления григорианской Пасхи (Алгоритм Меёса/Джонса/Бутчера)
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
        fixedHolidays = mutableMapOf(
            "0107" to Holiday(name = "christmas", priority = 1),
            "0114" to Holiday(name = "circumcision_of_the_lord", priority = 2),
            "0119" to Holiday(name = "theophany", priority = 1),

            // Февраль
            "0215" to Holiday(name = "presentation_of_the_lord", priority = 1),

            // Апрель
            "0407" to Holiday(name = "annunciation_of_the_most_holy_theotokos", priority = 1),

            // Май
            "0509" to Holiday(name = "memorial_day_for_fallen_soldiers", priority = 3),
            "0521" to Holiday(name = "john_the_apostle", priority = 3),

            // Июль
            "0707" to Holiday(name = "nativity_of_john_the_baptist", priority = 2),
            "0712" to Holiday(name = "feast_of_saints_peter_and_paul", priority = 2),

            // Август
            "0819" to Holiday(name = "transfiguration_of_the_lord", priority = 1),
            "0828" to Holiday(name = "dormition_of_the_most_holy_theotokos", priority = 1),

            // Сентябрь
            "0911" to Holiday(name = "beheading_of_john_the_baptist", priority = 2),
            "0921" to Holiday(name = "nativity_of_the_most_holy_theotokos", priority = 1),
            "0927" to Holiday(name = "exaltation_of_the_holy_cross", priority = 1),

            // Октябрь
            "1014" to Holiday(name = "protection_of_the_most_holy_theotokos", priority = 2),

            // Декабрь
            "1204" to Holiday(name = "entrance_of_the_most_holy_theotokos_into_the_temple", priority = 1)
        )
    }

    fun getCatholicFixedHolidays(){
        getWesternFixedHolidays()
        fixedHolidays.put("0101", Holiday(name = "solemnity_of_mary", priority = 1))
        // Февраль
        fixedHolidays.put("0222", Holiday(name = "chair_of_peter", priority = 2))
        // Май
        fixedHolidays.put("0503", Holiday(name = "philip_and_james", priority = 2))
        fixedHolidays.put("0514", Holiday(name = "matthias", priority = 2))
        // Июль
        fixedHolidays.put("0703", Holiday(name = "thomas", priority = 2))
        // Август
        fixedHolidays.put("0810", Holiday(name = "lawrence", priority = 2))
        fixedHolidays.put("0815", Holiday(name = "assumption", priority = 1))
        // Сентябрь
        fixedHolidays.put("0908", Holiday(name = "nativity_of_the_most_holy_theotokos", priority = 2))
        // Ноябрь
        fixedHolidays.put("1102", Holiday(name = "all_souls", priority = 1))
        fixedHolidays.put("1109", Holiday(name = "dedication_lateran_basilica", priority = 2))
        // Декабрь
        fixedHolidays.put("1208", Holiday(name = "immaculate_conception", priority = 1))
    }

    fun getLutheranFixedHolidays(){
        getWesternFixedHolidays()
        fixedHolidays.put("0101", Holiday(name = "circumcision_of_the_lord", priority = 1))
        fixedHolidays.put("0118", Holiday(name = "confession_of_peter", priority = 2))
        // Февраль
        fixedHolidays.put("0224", Holiday(name = "matthias", priority = 2))
        // Май
        fixedHolidays.put("0501", Holiday(name = "philip_and_james", priority = 2))
        // Июнь
        fixedHolidays.put("0625", Holiday(name = "augsburg_confession", priority = 2))
        // Август
        fixedHolidays.put("0815", Holiday(name = "mary_virgin", priority = 2))
        // Октябрь
        fixedHolidays.put("1031", Holiday(name = "reformation_day", priority = 2))
        // Декабрь
        fixedHolidays.put("1221", Holiday(name = "thomas", priority = 2))
        fixedHolidays.put("1224", Holiday(name = "christmas_vigil", priority = 2))
    }

    fun getWesternFixedHolidays(){
        fixedHolidays = mutableMapOf(
            "0106" to Holiday(name = "epiphany", priority = 1),
            "0125" to Holiday(name = "paul_conversion", priority = 1),

            // Февраль
            "0202" to Holiday(name = "presentation_of_the_lord", priority = 2),

            // Март
            "0319" to Holiday(name = "joseph", priority = 1),
            "0325" to Holiday(name = "annunciation_of_the_most_holy_theotokos", priority = 1),

            // Апрель
            "0425" to Holiday(name = "mark", priority = 2),

            // Май
            "0531" to Holiday(name = "visitation", priority = 2),

            // Июнь
            "0611" to Holiday(name = "barnabas", priority = 3),
            "0624" to Holiday(name = "nativity_of_john_the_baptist", priority = 1),
            "0629" to Holiday(name = "feast_of_saints_peter_and_paul", priority = 1),

            // Июль
            "0722" to Holiday(name = "magdalene", priority = 2),
            "0725" to Holiday(name = "james", priority = 2),

            // Август
            "0806" to Holiday(name = "transfiguration_of_the_lord", priority = 2),
            "0824" to Holiday(name = "bartholomew", priority = 2),
            "0829" to Holiday(name = "beheading_of_john_the_baptist", priority = 3),

            // Сентябрь
            "0914" to Holiday(name = "exaltation_of_the_holy_cross", priority = 2),
            "0921" to Holiday(name = "matthew", priority = 2),
            "0929" to Holiday(name = "archangels", priority = 2),

            // Октябрь
            "1018" to Holiday(name = "luke", priority = 2),
            "1028" to Holiday(name = "sumon_jude", priority = 2),

            // Ноябрь
            "1101" to Holiday(name = "all_saints", priority = 1),
            "1130" to Holiday(name = "andrew", priority = 2),

            // Декабрь
            "1225" to Holiday(name = "christmas", priority = 1),
            "1226" to Holiday(name = "stephen", priority = 2),
            "1227" to Holiday(name = "john_the_apostle", priority = 2),
            "1228" to Holiday(name = "innocents", priority = 2)
        )
    }

    // Вычисление всех подвижных праздников на основе даты Пасхи
    fun calculateBasicMovableHolidays(easterDate: LocalDate): MutableMap<String, LocalDate> {
        val holidays = mutableMapOf<String, LocalDate>()

        holidays["palm_sunday_greatfeast"] = easterDate.minusDays(7)
        holidays["holy_monday"] = easterDate.minusDays(6)
        holidays["holy_tuesday"] = easterDate.minusDays(5)
        holidays["holy_wednesday"] = easterDate.minusDays(4)
        holidays["holy_thursday"] = easterDate.minusDays(3)
        holidays["holy_friday"] = easterDate.minusDays(2)
        holidays["holy_saturday"] = easterDate.minusDays(1)
        // После Пасхи
        holidays["easter_monday"] = easterDate.plusDays(1)
        holidays["easter_tuesday"] = easterDate.plusDays(2)
        holidays["easter_wednesday"] = easterDate.plusDays(3)
        holidays["easter_thursday"] = easterDate.plusDays(4)
        holidays["easter_friday"] = easterDate.plusDays(5)
        holidays["easter_saturday"] = easterDate.plusDays(6)
        holidays["ascension_of_the_lord_greatfeast"] = easterDate.plusDays(39)
        holidays["pentecost_greatfeast"] = easterDate.plusDays(49)

        return holidays
    }

    fun calculateOrthodoxMovableHolidays(easterDate: LocalDate): MutableMap<String, LocalDate> {
        val holidays = calculateBasicMovableHolidays(easterDate)

        // Перед Пасхой
        holidays["forgiveness_sunday"] = easterDate.minusDays(49)
        holidays["triumph_of_orthodoxy"] = easterDate.minusDays(42)
        holidays["lent_saturday_5"] = easterDate.minusDays(15)
        holidays["lazarus_saturday"] = easterDate.minusDays(8)
        // После Пасхи
        holidays["thomas_sunday"] = easterDate.plusDays(7)
        holidays["day_of_the_holy_spirit"] = easterDate.plusDays(50)
        // Дни поминовения
        holidays["universal_memorial_saturday"] = easterDate.minusDays(57)
        holidays["lent_saturday_2_memorial"] = easterDate.minusDays(36)
        holidays["lent_saturday_3_memorial"] = easterDate.minusDays(29)
        holidays["lent_saturday_4_memorial"] = easterDate.minusDays(22)
        holidays["radonitsa_memorial"] = easterDate.plusDays(9)
        holidays["trinity_saturday_memorial"] = easterDate.plusDays(48)
        holidays["demetrius_saturday_memorial"] = LocalDate.of(easterDate.year, Month.NOVEMBER, 8).minusDays(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY))

        return holidays
    }

    fun calculateWesternMovableHolidays(easterDate: LocalDate): MutableMap<String, LocalDate> {
        val holidays = calculateBasicMovableHolidays(easterDate)

        // Перед Пасхой
        holidays["theophany"] = LocalDate.of(easterDate.year, 1, 6).with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
        holidays["ash_wednesday"] = easterDate.minusDays(46)
        // После Пасхи
        holidays["divine_mercy_sunday"] = easterDate.plusDays(7)
        holidays["day_of_the_holy_trinity_greatfeast"] = easterDate.plusDays(56)
        holidays["corpus_christi_greatfeast"] = easterDate.plusDays(60)

        val christmas = LocalDate.of(easterDate.year, 12, 25)

        holidays["advent_1_sunday"] = christmas.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).minusWeeks(4)
        holidays["advent_2_sunday"] = christmas.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).minusWeeks(3)
        holidays["advent_3_sunday"] = christmas.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).minusWeeks(2)
        holidays["advent_4_sunday"] = christmas.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).minusWeeks(1)
        return holidays
    }

    fun calculateCatholicMovableHolidays(easterDate: LocalDate): MutableMap<String, LocalDate> {
        val holidays = calculateWesternMovableHolidays(easterDate)

        holidays["mary_virgin"] = easterDate.plusDays(50)
        holidays["jesus_heart_greatfeast"] = easterDate.plusDays(68)
        holidays["mary_heart_memorial"] = easterDate.plusDays(69)

        val christmas = LocalDate.of(easterDate.year, 12, 25)

        holidays["jesus_king_greatfeast"] = christmas.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).minusWeeks(5)
        holidays["holy_family"] = if (christmas.dayOfWeek == DayOfWeek.SUNDAY) LocalDate.of(easterDate.year, 12, 30) else christmas.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
        return holidays
    }

    // Получение уровня поста для даты
    fun getFastLevel(date: LocalDate, easterDate: LocalDate, confession: String): FastLevel {
        if (confession == "cat"){
            if (date == easterDate.minusDays(46) || date == easterDate.minusDays(2)){
                return FastLevel.FAST
            } else if (date.dayOfWeek == DayOfWeek.FRIDAY){
                return FastLevel.ABSTINENCE
            }
            return FastLevel.NO_FAST
        }
        if (confession == "lut"){
            return FastLevel.NO_FAST
        }
        val dayOfWeek = date.dayOfWeek.value

        // Проверяем сплошные седмицы
        if (isContinuousWeek(date, easterDate)) {
            return FastLevel.CONTINUOUS_WEEK
        }

        // Великий пост
        if (isGreatLent(date, easterDate)) {
            return when {
                date == easterDate.minusDays(7) || date == LocalDate.of(easterDate.year, 4, 7) -> FastLevel.FISH
                date.dayOfWeek.value == 7 || date.dayOfWeek.value == 6 -> FastLevel.OIL_ALLOWED
                date.dayOfWeek.value == 1 || date.dayOfWeek.value == 2 || date.dayOfWeek.value == 4 -> FastLevel.NO_OIL
                else -> FastLevel.XEROPHAGY
            }
        }

        // Рождественский пост
        if (isChristmasFast(date, date.year)) {
            return when {
                date == LocalDate.of(easterDate.year, 12, 4) && (date.dayOfWeek.value == 3 || date.dayOfWeek.value == 5) -> FastLevel.FISH
                date.isBefore(LocalDate.of(date.year, 12, 20)) && (date.dayOfWeek.value == 3 || date.dayOfWeek.value == 5) -> FastLevel.OIL_ALLOWED
                date.isAfter(LocalDate.of(date.year, 12, 19)) && date.isBefore(LocalDate.of(date.year, 1, 2)) && (date.dayOfWeek.value == 3 || date.dayOfWeek.value == 5) -> FastLevel.NO_OIL
                date.isAfter(LocalDate.of(date.year, 12, 19)) && date.isBefore(LocalDate.of(date.year, 1, 2)) && (date.dayOfWeek.value == 1 || date.dayOfWeek.value == 2 || date.dayOfWeek.value == 4) -> FastLevel.OIL_ALLOWED
                date.isAfter(LocalDate.of(date.year, 1, 1)) && (date.dayOfWeek.value == 3 || date.dayOfWeek.value == 5) -> FastLevel.XEROPHAGY
                date.isAfter(LocalDate.of(date.year, 1, 1)) && (date.dayOfWeek.value == 1 || date.dayOfWeek.value == 2 || date.dayOfWeek.value == 4) -> FastLevel.NO_OIL
                date.isAfter(LocalDate.of(date.year, 1, 1)) && (date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7) -> FastLevel.OIL_ALLOWED
                else -> FastLevel.FISH
            }
        }

        // Петров пост
        if (isPeterFast(date, easterDate)) {
            return when {
                date.dayOfWeek.value == 3 || date.dayOfWeek.value == 5 -> FastLevel.OIL_ALLOWED
                else -> FastLevel.FISH
            }
        }

        // Успенский пост
        if (isDormitionFast(date, date.year)) {
            return when {
                date == LocalDate.of(easterDate.year, 8, 19) -> FastLevel.FISH
                date.dayOfWeek.value == 3 || date.dayOfWeek.value == 5 -> FastLevel.XEROPHAGY
                date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7 -> FastLevel.OIL_ALLOWED
                else -> FastLevel.NO_OIL
            }
        }

        // Послабления по уставу
        val specialDates = listOf(
            LocalDate.of(easterDate.year, 2, 15),
            LocalDate.of(easterDate.year, 8, 19),
            LocalDate.of(easterDate.year, 9, 21),
            LocalDate.of(easterDate.year, 10, 14),
            LocalDate.of(easterDate.year, 12, 4),
            LocalDate.of(easterDate.year, 7, 7),
            LocalDate.of(easterDate.year, 7, 12),
            LocalDate.of(easterDate.year, 5, 21),
            LocalDate.of(easterDate.year, 8, 28)
        )

        if (((date.isAfter(easterDate) && date.isBefore(easterDate.plusDays(49))) || (date in specialDates)) && (date.dayOfWeek.value == 3 || date.dayOfWeek.value == 5)) {
            return FastLevel.FISH
        }

        if (isOneDayFast(date)) {
            return FastLevel.OIL_ALLOWED
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

    private fun isOneDayFast(date: LocalDate): Boolean{
        return (date.month == Month.JANUARY && date.dayOfMonth == 18) || (date.month == Month.SEPTEMBER && date.dayOfMonth == 11) || (date.month == Month.SEPTEMBER && date.dayOfMonth == 27)
    }

    suspend fun getMonthCalendar(year: Int, month: Int, confession: String): List<CalendarDay> = withContext(Dispatchers.IO) {
        val days = mutableListOf<CalendarDay>()
        val today = LocalDate.now()
        val easterDate : LocalDate
        val movableHolidays: MutableMap<String, LocalDate>
        if (confession == "ort"){
            easterDate = calculateOrthodoxEaster(year)
        } else {
            easterDate = calculateGregorianEaster(year)
        }
        when (confession){
            "ort" -> { getOrthodoxFixedHolidays(); movableHolidays = calculateOrthodoxMovableHolidays(easterDate) }
            "cat" -> { getCatholicFixedHolidays(); movableHolidays = calculateCatholicMovableHolidays(easterDate) }
            else -> { getLutheranFixedHolidays(); movableHolidays = calculateWesternMovableHolidays(easterDate) }
        }

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
                    val priority = if (name.contains("greatfeast")) 1 else if (name.contains("memorial")) 3 else 2
                    dayHolidays.add(
                        Holiday(
                            name = name,
                            priority = priority
                    ))
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

            val fastLevel = getFastLevel(currentDate, easterDate, confession)

            days.add(
                CalendarDay(
                    date = currentDate,
                    holidays = dayHolidays.sortedBy { it.priority },
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
        val movableHolidays: MutableMap<String, LocalDate>
        when (confession){
            "ort" -> getOrthodoxFixedHolidays()
            "cat" -> getCatholicFixedHolidays()
            else -> getLutheranFixedHolidays()
        }
        // Вычисляем Пасху для года
        if (confession == "ort"){
            easterDate = calculateOrthodoxEaster(currentDate.year)
        } else{
            easterDate = calculateGregorianEaster(currentDate.year)
        }
        when (confession){
            "ort" -> { getOrthodoxFixedHolidays(); movableHolidays = calculateOrthodoxMovableHolidays(easterDate) }
            "cat" -> { getCatholicFixedHolidays(); movableHolidays = calculateCatholicMovableHolidays(easterDate) }
            else -> { getLutheranFixedHolidays(); movableHolidays = calculateWesternMovableHolidays(easterDate) }
        }
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
                val priority = if (name.contains("greatfeast")) 1 else if (name.contains("memorial")) 3 else 2
                dayHolidays.add(
                    Holiday(
                        name = name,
                        priority = priority)
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

        val fastLevel = getFastLevel(currentDate, easterDate, confession)

        return@withContext CalendarDay(
            date = currentDate,
            holidays = dayHolidays.sortedBy { it.priority },
            fastLevel = fastLevel,
            isToday = true,
            isSelected = false
        )
    }
}