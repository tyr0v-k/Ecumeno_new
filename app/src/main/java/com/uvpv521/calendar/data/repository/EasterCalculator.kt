package com.uvpv521.calendar.data.repository

import com.uvpv521.calendar.data.models.FastLevel
import java.time.LocalDate
import java.time.Month

object EasterCalculator {

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
    /**
     * Вычисление всех подвижных праздников на основе даты Пасхи
     */
    fun calculateMovableHolidays(easterDate: LocalDate, year: Int): Map<String, LocalDate> {
        val holidays = mutableMapOf<String, LocalDate>()

        // Неделя перед Пасхой
        holidays["Вербное воскресенье"] = easterDate.minusDays(7)
        holidays["Лазарева суббота"] = easterDate.minusDays(8)

        // Великий пост
        holidays["Страстная пятница"] = easterDate.minusDays(2)

        // После Пасхи
        holidays["Радоница"] = easterDate.plusDays(9)
        holidays["Вознесение Господне"] = easterDate.plusDays(39)
        holidays["День Святой Троицы"] = easterDate.plusDays(49)
        holidays["День Святого Духа"] = easterDate.plusDays(50)

        // Мясопустная и Сырная седмицы
        holidays["Вселенская родительская суббота"] = easterDate.minusDays(57)
        holidays["Прощеное воскресенье"] = easterDate.minusDays(49)

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
            return FastLevel.STRICT_FAST
        }

        // Среда и пятница - постные дни (кроме сплошных седмиц)
        if (dayOfWeek == 3 || dayOfWeek == 5) {
            return FastLevel.XEROPHAGY
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
        val brightWeekStart = easterDate
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
}