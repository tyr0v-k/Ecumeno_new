package com.ecumeno.core.calculator

import com.ecumeno.data.local.preferences.Confession
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.time.LocalDate

class EasterCalculatorTest {
    @Test
    fun `orthodox easter 2024 is May 5`() {
        val result = EasterCalculator.calculateOrthodoxEaster(2024)

        assertEquals(2024, result.year)
        assertEquals(5, result.monthValue)
        assertEquals(5, result.dayOfMonth)
    }

    @Test
    fun `orthodox easter 2025 is April 20`() {
        val result = EasterCalculator.calculateOrthodoxEaster(2025)

        assertEquals(2025, result.year)
        assertEquals(4, result.monthValue)
        assertEquals(20, result.dayOfMonth)
    }

    @Test
    fun `catholic easter 2024 is March 31`() {
        val result = EasterCalculator.calculateGregorianEaster(2024)

        assertEquals(2024, result.year)
        assertEquals(3, result.monthValue)
        assertEquals(31, result.dayOfMonth)
    }

    @Test
    fun `catholic easter always between March 22 and April 25`() {
        for (year in 2020..2030) {
            val result = EasterCalculator.calculateGregorianEaster(year)

            val isValid = (result.monthValue == 3 && result.dayOfMonth >= 22) ||
                    (result.monthValue == 4 && result.dayOfMonth <= 25)

            assertTrue(
                "Easter $year failed: ${result.monthValue}/${result.dayOfMonth}",
                isValid
            )
        }
    }

    @Test
    fun `orthodox easter never before catholic easter`() {
        for (year in 2020..2030) {
            val catholic = EasterCalculator.calculateGregorianEaster(year)
            val orthodox = EasterCalculator.calculateOrthodoxEaster(year)

            val cathDate = LocalDate.of(catholic.year, catholic.monthValue, catholic.dayOfMonth)
            val ortDate = LocalDate.of(orthodox.year, orthodox.monthValue, orthodox.dayOfMonth)

            assertTrue(
                "Orthodox $year ($ortDate) before Catholic ($cathDate)",
                !ortDate.isBefore(cathDate)
            )
        }
    }

    @Test
    fun `getDailyCalendar returns today date`(){
        val result = EasterCalculator.getDailyCalendar(Confession.ort)

        assertNotNull(result)
        assertEquals(LocalDate.now(), result.date)
    }

    @Test
    fun `2026 May 31 is Orthodox Pentecost`(){
        val result = EasterCalculator.getMonthCalendar(2026, 5, Confession.ort)

        val day = result.first { day -> day.date == LocalDate.of(2026, 5, 31) }
        assertNotNull(result)
        assertEquals("pentecost_greatfeast", day.holidays[0].name)
    }

    @Test
    fun `orthodox easter 2026 is April 12`() {
        val result = EasterCalculator.calculateOrthodoxEaster(2026)

        assertEquals(2026, result.year)
        assertEquals(4, result.monthValue)
        assertEquals(12, result.dayOfMonth)
    }
}