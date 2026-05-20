package com.ecumeno.core.calculator

import com.ecumeno.core.domain.CalendarDay
import com.ecumeno.core.domain.FastLevel
import com.ecumeno.core.domain.Holiday
import com.ecumeno.core.domain.Confession
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.temporal.TemporalAdjusters

object EasterCalculator {
    lateinit var fixedHolidays : MutableMap<String, Holiday>
    fun calculateOrthodoxEaster(year: Int): LocalDate {
        val a = year % 19
        val b = year % 4
        val c = year % 7
        val d = (19 * a + 15) % 30
        val e = (2 * b + 4 * c + 6 * d + 6) % 7
        val f = d + e

        val day = if (f <= 9) {
            22 + f
        } else {
            f - 9
        }
        val month = if (f <= 9) Month.MARCH else Month.APRIL

        var date = LocalDate.of(year, month, day).plusDays(13)

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
        fixedHolidays = mutableMapOf(
            "0107" to Holiday(name = "christmas", priority = 1),
            "0114" to Holiday(name = "circumcision_of_the_lord", priority = 2),
            "0119" to Holiday(name = "theophany_lord", priority = 1),
            "0215" to Holiday(name = "presentation_of_the_lord", priority = 1),
            "0407" to Holiday(name = "annunciation_of_the_most_holy_theotokos", priority = 1),
            "0509" to Holiday(name = "memorial_day_for_fallen_soldiers", priority = 3),
            "0521" to Holiday(name = "john_the_apostle", priority = 3),
            "0707" to Holiday(name = "nativity_of_john_the_baptist", priority = 2),
            "0712" to Holiday(name = "feast_of_saints_peter_and_paul", priority = 2),
            "0819" to Holiday(name = "transfiguration_of_the_lord", priority = 1),
            "0828" to Holiday(name = "dormition_of_the_most_holy_theotokos", priority = 1),
            "0911" to Holiday(name = "beheading_of_john_the_baptist", priority = 2),
            "0921" to Holiday(name = "nativity_of_the_most_holy_theotokos", priority = 1),
            "0927" to Holiday(name = "exaltation_of_the_holy_cross_lord", priority = 1),
            "1014" to Holiday(name = "protection_of_the_most_holy_theotokos", priority = 2),
            "1204" to Holiday(
                name = "entrance_of_the_most_holy_theotokos_into_the_temple",
                priority = 1
            )
        )
    }

    fun getCatholicFixedHolidays(){
        getWesternFixedHolidays()
        fixedHolidays.put("0101", Holiday(name = "solemnity_of_mary", priority = 1))
        fixedHolidays.put("0222", Holiday(name = "chair_of_peter", priority = 2))
        fixedHolidays.put("0503", Holiday(name = "philip_and_james", priority = 2))
        fixedHolidays.put("0514", Holiday(name = "matthias", priority = 2))
        fixedHolidays.put("0703", Holiday(name = "thomas", priority = 2))
        fixedHolidays.put("0810", Holiday(name = "lawrence", priority = 2))
        fixedHolidays.put("0815", Holiday(name = "assumption", priority = 1))
        fixedHolidays.put("0908",
            Holiday(name = "nativity_of_the_most_holy_theotokos", priority = 2)
        )
        fixedHolidays.put("1102", Holiday(name = "all_souls", priority = 1))
        fixedHolidays.put("1109", Holiday(name = "dedication_lateran_basilica_lord", priority = 2))
        fixedHolidays.put("1208", Holiday(name = "immaculate_conception", priority = 1))
    }

    fun getLutheranFixedHolidays(){
        getWesternFixedHolidays()
        fixedHolidays.put("0101", Holiday(name = "circumcision_of_the_lord", priority = 1))
        fixedHolidays.put("0118", Holiday(name = "confession_of_peter", priority = 3))
        fixedHolidays.put("0224", Holiday(name = "matthias", priority = 2))
        fixedHolidays.put("0503", Holiday(name = "philip_and_james", priority = 2))
        fixedHolidays.put("0625", Holiday(name = "augsburg_confession", priority = 2))
        fixedHolidays.put("0815", Holiday(name = "mary_virgin", priority = 2))
        fixedHolidays.put("1031", Holiday(name = "reformation_day", priority = 2))
        fixedHolidays.put("1221", Holiday(name = "thomas", priority = 2))
        fixedHolidays.put("1224", Holiday(name = "christmas_vigil", priority = 2))
    }

    fun getWesternFixedHolidays(){
        fixedHolidays = mutableMapOf(
            "0106" to Holiday(name = "epiphany", priority = 1),
            "0125" to Holiday(name = "paul_conversion", priority = 1),
            "0202" to Holiday(name = "presentation_of_the_lord", priority = 2),
            "0319" to Holiday(name = "joseph", priority = 2),
            "0325" to Holiday(name = "annunciation_of_the_most_holy_theotokos", priority = 1),
            "0425" to Holiday(name = "mark", priority = 2),
            "0531" to Holiday(name = "visitation", priority = 2),
            "0611" to Holiday(name = "barnabas", priority = 3),
            "0624" to Holiday(name = "nativity_of_john_the_baptist", priority = 1),
            "0629" to Holiday(name = "feast_of_saints_peter_and_paul", priority = 1),
            "0722" to Holiday(name = "magdalene", priority = 2),
            "0725" to Holiday(name = "james", priority = 2),
            "0806" to Holiday(name = "transfiguration_of_the_lord", priority = 2),
            "0824" to Holiday(name = "bartholomew", priority = 2),
            "0829" to Holiday(name = "beheading_of_john_the_baptist", priority = 3),
            "0914" to Holiday(name = "exaltation_of_the_holy_cross_lord", priority = 2),
            "0921" to Holiday(name = "matthew", priority = 2),
            "0929" to Holiday(name = "archangels", priority = 2),
            "1018" to Holiday(name = "luke", priority = 2),
            "1028" to Holiday(name = "sumon_jude", priority = 2),
            "1101" to Holiday(name = "all_saints", priority = 1),
            "1130" to Holiday(name = "andrew", priority = 2),
            "1225" to Holiday(name = "christmas", priority = 1),
            "1226" to Holiday(name = "stephen", priority = 2),
            "1227" to Holiday(name = "john_the_apostle", priority = 2),
            "1228" to Holiday(name = "innocents", priority = 2)
        )
    }

    fun moveCatholicFixedHolidays(easterDate: LocalDate){
        val josephDate = LocalDate.of(easterDate.year, 3, 19)
        val annunciationDate = LocalDate.of(easterDate.year, 3, 25)
        if (annunciationDate.isAfter(easterDate.minusDays(46)) && annunciationDate.isBefore(easterDate.minusDays(7)) && annunciationDate.dayOfWeek == DayOfWeek.SUNDAY){
            fixedHolidays.remove("0325")
            fixedHolidays.put("0326",
                Holiday(name = "annunciation_of_the_most_holy_theotokos", priority = 1)
            )
        } else if (annunciationDate.isAfter(easterDate.minusDays(8)) && annunciationDate.isBefore(easterDate.plusDays(7))){
            fixedHolidays.remove("0325")
            fixedHolidays.put((if (easterDate.plusDays(8).dayOfMonth < 10) "0" + easterDate.plusDays(8).monthValue + "0" else "0" + easterDate.plusDays(8).monthValue) + easterDate.plusDays(8).dayOfMonth,
                Holiday(name = "annunciation_of_the_most_holy_theotokos", priority = 1)
            )
        }
        if (josephDate.isAfter(easterDate.minusDays(46)) && josephDate.isBefore(easterDate.minusDays(7)) && annunciationDate.dayOfWeek == DayOfWeek.SUNDAY){
            fixedHolidays.remove("0319")
            fixedHolidays.put("0320", Holiday(name = "joseph", priority = 1))
        } else if (josephDate.isAfter(easterDate.minusDays(8)) && josephDate.isBefore(easterDate)){
            fixedHolidays.remove("0319")
            fixedHolidays.put("03" + easterDate.minusDays(8).dayOfMonth,
                Holiday(name = "joseph", priority = 1)
            )
        }
    }

    fun moveLutheranFixedHolidays(easterDate: LocalDate){
        val annunciationDate = LocalDate.of(easterDate.year, 3, 25)
        if (annunciationDate.isAfter(easterDate.minusDays(8)) && annunciationDate.isBefore(easterDate.plusDays(7))){
            fixedHolidays.remove("0325")
            fixedHolidays.put("03" + easterDate.minusDays(7).with(
                TemporalAdjusters.previous(
                DayOfWeek.SUNDAY)).dayOfMonth,
                Holiday(name = "annunciation_of_the_most_holy_theotokos", priority = 1)
            )
        }
    }

    fun calculateBasicMovableHolidays(easterDate: LocalDate): MutableMap<String, LocalDate> {
        val holidays = mutableMapOf<String, LocalDate>()

        holidays["palm_sunday_greatfeast"] = easterDate.minusDays(7)
        holidays["reserved_holy_monday"] = easterDate.minusDays(6)
        holidays["reserved_holy_tuesday"] = easterDate.minusDays(5)
        holidays["reserved_holy_wednesday"] = easterDate.minusDays(4)
        holidays["reserved_holy_thursday"] = easterDate.minusDays(3)
        holidays["reserved_holy_friday"] = easterDate.minusDays(2)
        holidays["reserved_holy_saturday"] = easterDate.minusDays(1)
        holidays["reserved_easter_monday"] = easterDate.plusDays(1)
        holidays["reserved_easter_tuesday"] = easterDate.plusDays(2)
        holidays["reserved_easter_wednesday"] = easterDate.plusDays(3)
        holidays["reserved_easter_thursday"] = easterDate.plusDays(4)
        holidays["reserved_easter_friday"] = easterDate.plusDays(5)
        holidays["reserved_easter_saturday"] = easterDate.plusDays(6)
        holidays["ascension_of_the_lord_greatfeast"] = easterDate.plusDays(39)
        holidays["pentecost_greatfeast"] = easterDate.plusDays(49)

        return holidays
    }

    fun calculateOrthodoxMovableHolidays(easterDate: LocalDate): MutableMap<String, LocalDate> {
        val holidays = calculateBasicMovableHolidays(easterDate)

        holidays["forgiveness_sunday"] = easterDate.minusDays(49)
        holidays["triumph_of_orthodoxy"] = easterDate.minusDays(42)
        holidays["lent_saturday_5"] = easterDate.minusDays(15)
        holidays["lazarus_saturday"] = easterDate.minusDays(8)
        holidays["thomas_sunday"] = easterDate.plusDays(7)
        holidays["day_of_the_holy_spirit"] = easterDate.plusDays(50)
        holidays["universal_memorial_saturday"] = easterDate.minusDays(57)
        holidays["lent_saturday_2_memorial"] = easterDate.minusDays(36)
        holidays["lent_saturday_3_memorial"] = easterDate.minusDays(29)
        holidays["lent_saturday_4_memorial"] = easterDate.minusDays(22)
        holidays["radonitsa_memorial"] = easterDate.plusDays(9)
        holidays["trinity_saturday_memorial"] = easterDate.plusDays(48)
        holidays["demetrius_saturday_memorial"] = LocalDate.of(easterDate.year, Month.NOVEMBER, 8).minusDays(1).with(
            TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY))

        return holidays
    }

    fun calculateWesternMovableHolidays(easterDate: LocalDate): MutableMap<String, LocalDate> {
        val holidays = calculateBasicMovableHolidays(easterDate)

        holidays["theophany_lord"] = LocalDate.of(easterDate.year, 1, 6).with(
            TemporalAdjusters.next(
                DayOfWeek.SUNDAY))
        holidays["ash_wednesday"] = easterDate.minusDays(46)
        holidays["reserved_divine_mercy_sunday"] = easterDate.plusDays(7)
        holidays["day_of_the_holy_trinity_greatfeast"] = easterDate.plusDays(56)
        holidays["corpus_christi_greatfeast"] = easterDate.plusDays(60)

        val christmas = LocalDate.of(easterDate.year, 12, 25)

        holidays["reserved_advent_1_sunday"] = christmas.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.SUNDAY)).minusWeeks(3)
        holidays["reserved_advent_2_sunday"] = christmas.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.SUNDAY)).minusWeeks(2)
        holidays["reserved_advent_3_sunday"] = christmas.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.SUNDAY)).minusWeeks(1)
        holidays["reserved_advent_4_sunday"] = christmas.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.SUNDAY))
        return holidays
    }

    fun calculateCatholicMovableHolidays(easterDate: LocalDate): MutableMap<String, LocalDate> {
        val holidays = calculateWesternMovableHolidays(easterDate)

        holidays["mary_virgin"] = easterDate.plusDays(50)
        holidays["jesus_heart_greatfeast"] = easterDate.plusDays(68)
        holidays["mary_heart_memorial"] = easterDate.plusDays(69)

        val christmas = LocalDate.of(easterDate.year, 12, 25)

        holidays["jesus_king_greatfeast"] = christmas.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.SUNDAY)).minusWeeks(4)
        holidays["holy_family_lord"] = if (christmas.dayOfWeek == DayOfWeek.SUNDAY) LocalDate.of(easterDate.year, 12, 30) else christmas.with(
            TemporalAdjusters.next(DayOfWeek.SUNDAY))
        return holidays
    }

    fun getFastLevel(date: LocalDate, easterDate: LocalDate, confession: Confession): FastLevel {
        when (confession){
            Confession.cat -> {
                if (date == easterDate.minusDays(46) || date == easterDate.minusDays(2)){
                    return FastLevel.FAST
                } else if (date.dayOfWeek == DayOfWeek.FRIDAY){
                    return FastLevel.ABSTINENCE
                }
                return FastLevel.NO_FAST
            }
            Confession.lut -> return FastLevel.NO_FAST
            Confession.ort -> {
                val dayOfWeek = date.dayOfWeek.value

                if (isContinuousWeek(date, easterDate)) {
                    return FastLevel.CONTINUOUS_WEEK
                }

                if (isGreatLent(date, easterDate)) {
                    return when {
                        date == easterDate.minusDays(7) || date == LocalDate.of(easterDate.year, 4, 7) -> FastLevel.FISH
                        date.dayOfWeek.value == 7 || date.dayOfWeek.value == 6 -> FastLevel.OIL_ALLOWED
                        date.dayOfWeek.value == 1 || date.dayOfWeek.value == 2 || date.dayOfWeek.value == 4 -> FastLevel.NO_OIL
                        else -> FastLevel.XEROPHAGY
                    }
                }

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

                if (isPeterFast(date, easterDate)) {
                    return when {
                        date.dayOfWeek.value == 3 || date.dayOfWeek.value == 5 -> FastLevel.OIL_ALLOWED
                        else -> FastLevel.FISH
                    }
                }

                if (isDormitionFast(date, date.year)) {
                    return when {
                        date == LocalDate.of(easterDate.year, 8, 19) -> FastLevel.FISH
                        date.dayOfWeek.value == 3 || date.dayOfWeek.value == 5 -> FastLevel.XEROPHAGY
                        date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7 -> FastLevel.OIL_ALLOWED
                        else -> FastLevel.NO_OIL
                    }
                }

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

                if (dayOfWeek == 3 || dayOfWeek == 5) {
                    return FastLevel.FAST
                }
                return FastLevel.NO_FAST
            }
        }
    }

    private fun isGreatLent(date: LocalDate, easterDate: LocalDate): Boolean {
        val start = easterDate.minusDays(48)
        val end = easterDate.minusDays(1)
        return !date.isBefore(start) && !date.isAfter(end)
    }

    private fun isContinuousWeek(date: LocalDate, easterDate: LocalDate): Boolean {
        val year = date.year

        val christmastideStart = LocalDate.of(year, 1, 7)
        val christmastideEnd = LocalDate.of(year, 1, 17)
        val publicanWeekStart = easterDate.minusDays(69)
        val publicanWeekEnd = easterDate.minusDays(63)
        val cheeseFareWeekStart = easterDate.minusDays(55)
        val cheeseFareWeekEnd = easterDate.minusDays(49)
        val brightWeekStart = easterDate.plusDays(1)
        val brightWeekEnd = easterDate.plusDays(6)
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

    fun getMonthCalendar(year: Int, month: Int, confession: Confession): List<CalendarDay>{
        val days = mutableListOf<CalendarDay>()
        val today = LocalDate.now()
        val movableHolidays: MutableMap<String, LocalDate>
        val easterDate = if (confession == Confession.ort) {
            calculateOrthodoxEaster(year)
        } else {
            calculateGregorianEaster(year)
        }
        when (confession) {
            Confession.ort -> {
                getOrthodoxFixedHolidays(); movableHolidays =
                    calculateOrthodoxMovableHolidays(easterDate)
            }

            Confession.cat -> {
                getCatholicFixedHolidays(); movableHolidays =
                    calculateCatholicMovableHolidays(easterDate); moveCatholicFixedHolidays(
                    easterDate
                )
            }

            Confession.lut -> {
                getLutheranFixedHolidays(); movableHolidays =
                    calculateWesternMovableHolidays(easterDate); moveLutheranFixedHolidays(
                    easterDate
                )
            }
        }

        val firstDay = LocalDate.of(year, month, 1)
        val lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth())

        var currentDate = firstDay.with(DayOfWeek.MONDAY)
        if (firstDay.dayOfWeek != DayOfWeek.MONDAY) {
            currentDate = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        }

        val times =
            if ((firstDay.dayOfWeek == DayOfWeek.SUNDAY && lastDay.dayOfMonth >= 30) || (firstDay.dayOfWeek == DayOfWeek.SATURDAY && lastDay.dayOfMonth == 31)) {
                42
            } else {
                35
            }

        repeat(times) {
            val dayHolidays = mutableListOf<Holiday>()
            val monthDay =
                String.format("%02d%02d", currentDate.monthValue, currentDate.dayOfMonth)
            fixedHolidays[monthDay]?.let { fixedHoliday -> dayHolidays.add(fixedHoliday) }

            movableHolidays.forEach { (name, date) ->
                if (date.year == currentDate.year &&
                    date.monthValue == currentDate.monthValue &&
                    date.dayOfMonth == currentDate.dayOfMonth
                ) {
                    val priority =
                        if (name.contains("greatfeast")) 1 else if (name.contains("memorial")) 3 else 2
                    dayHolidays.add(
                        Holiday(
                            name = name,
                            priority = priority
                        )
                    )
                }
            }

            if (currentDate == easterDate) {
                dayHolidays.add(
                    Holiday(
                        name = "easter",
                        priority = 0
                    )
                )
            }

            val fastLevel = getFastLevel(currentDate, easterDate, confession)

            if (confession == Confession.cat && dayHolidays.isNotEmpty()) {
                val maxPriority = dayHolidays.sortedBy { it.priority }[0].priority
                dayHolidays.removeIf { holiday ->
                    (!holiday.name.contains("easter") && !holiday.name.contains(
                        "reserved_"
                    )) && (currentDate.isAfter(easterDate.minusDays(7)) && currentDate.isBefore(
                        easterDate.plusDays(8)
                    ))
                }
                dayHolidays.removeIf { holiday -> (maxPriority < 3 || currentDate.dayOfWeek == DayOfWeek.SUNDAY) && holiday.priority == 3 }
                dayHolidays.removeIf { holiday ->
                    (!holiday.name.contains("easter") && !holiday.name.contains(
                        "reserved_"
                    ) && !holiday.name.contains("_lord")) && holiday.priority == 2 && (maxPriority < 2 || currentDate.dayOfWeek == DayOfWeek.SUNDAY)
                }
            }

            days.add(
                CalendarDay(
                    date = currentDate,
                    holidays = dayHolidays.sortedBy { it.priority },
                    fastLevel = fastLevel,
                    isToday = currentDate == today,
                    currentMonth = month == currentDate.month.value
                )
            )

            currentDate = currentDate.plusDays(1)
        }

        return days
    }

    fun getDailyCalendar(confession: Confession): CalendarDay{
        val currentDate = LocalDate.now()
        val easterDate: LocalDate
        val movableHolidays: MutableMap<String, LocalDate>
        if (confession == Confession.ort) {
            easterDate = calculateOrthodoxEaster(currentDate.year)
        } else {
            easterDate = calculateGregorianEaster(currentDate.year)
        }
        when (confession) {
            Confession.ort -> {
                getOrthodoxFixedHolidays(); movableHolidays =
                    calculateOrthodoxMovableHolidays(easterDate)
            }

            Confession.cat -> {
                getCatholicFixedHolidays(); movableHolidays =
                    calculateCatholicMovableHolidays(easterDate); moveCatholicFixedHolidays(
                    easterDate
                )
            }

            Confession.lut -> {
                getLutheranFixedHolidays(); movableHolidays =
                    calculateWesternMovableHolidays(easterDate); moveLutheranFixedHolidays(
                    easterDate
                )
            }
        }
        val dayHolidays = mutableListOf<Holiday>()

        val monthDay = String.format("%02d%02d", currentDate.monthValue, currentDate.dayOfMonth)
        fixedHolidays[monthDay]?.let { fixedHoliday -> dayHolidays.add(fixedHoliday) }

        movableHolidays.forEach { (name, date) ->
            if (date.year == currentDate.year &&
                date.monthValue == currentDate.monthValue &&
                date.dayOfMonth == currentDate.dayOfMonth
            ) {
                val priority =
                    if (name.contains("greatfeast")) 1 else if (name.contains("memorial")) 3 else 2
                dayHolidays.add(
                    Holiday(
                        name = name,
                        priority = priority
                    )
                )
            }
        }

        if (currentDate == easterDate) {
            dayHolidays.add(
                Holiday(
                    name = "easter",
                    priority = 0
                )
            )
        }

        val fastLevel = getFastLevel(currentDate, easterDate, confession)

        if (confession == Confession.cat && dayHolidays.isNotEmpty()) {
            val maxPriority = dayHolidays.sortedBy { it.priority }[0].priority
            dayHolidays.removeIf { holiday ->
                (!holiday.name.contains("easter") && !holiday.name.contains(
                    "reserved_"
                )) && (currentDate.isAfter(easterDate.minusDays(7)) && currentDate.isBefore(
                    easterDate.plusDays(8)
                ))
            }
            dayHolidays.removeIf { holiday -> (maxPriority < 3 || currentDate.dayOfWeek == DayOfWeek.SUNDAY) && holiday.priority == 3 }
            dayHolidays.removeIf { holiday ->
                (!holiday.name.contains("easter") && !holiday.name.contains(
                    "reserved_"
                ) && !holiday.name.contains("_lord")) && holiday.priority == 2 && (maxPriority < 2 || currentDate.dayOfWeek == DayOfWeek.SUNDAY)
            }
        }

        return CalendarDay(
            date = currentDate,
            holidays = dayHolidays.sortedBy { it.priority },
            fastLevel = fastLevel,
            isToday = true,
        )
    }
}