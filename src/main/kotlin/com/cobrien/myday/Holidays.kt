package com.cobrien.myday

import java.time.LocalDate
import java.time.Month
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

/**
 * Holiday data class
 */
data class Holiday(
    val name: String,
    val date: LocalDate,
    val type: HolidayType = HolidayType.FEDERAL
)

enum class HolidayType {
    FEDERAL,      // US Federal holidays
    OBSERVANCE,   // Common observances (Valentine's, Halloween, etc.)
    RELIGIOUS,    // Religious holidays
    INTERNATIONAL // International holidays
}

/**
 * Holiday provider for US holidays
 * Can be extended to support other countries
 */
object HolidayProvider {

    /**
     * Get all holidays for a given year
     */
    fun getHolidays(year: Int): List<Holiday> {
        val holidays = mutableListOf<Holiday>()

        // Fixed date holidays
        holidays.add(Holiday("New Year's Day", LocalDate.of(year, Month.JANUARY, 1), HolidayType.FEDERAL))
        holidays.add(Holiday("Valentine's Day", LocalDate.of(year, Month.FEBRUARY, 14), HolidayType.OBSERVANCE))
        holidays.add(Holiday("St. Patrick's Day", LocalDate.of(year, Month.MARCH, 17), HolidayType.OBSERVANCE))
        holidays.add(Holiday("April Fools' Day", LocalDate.of(year, Month.APRIL, 1), HolidayType.OBSERVANCE))
        holidays.add(Holiday("Earth Day", LocalDate.of(year, Month.APRIL, 22), HolidayType.OBSERVANCE))
        holidays.add(Holiday("Cinco de Mayo", LocalDate.of(year, Month.MAY, 5), HolidayType.OBSERVANCE))
        holidays.add(Holiday("Independence Day", LocalDate.of(year, Month.JULY, 4), HolidayType.FEDERAL))
        holidays.add(Holiday("Halloween", LocalDate.of(year, Month.OCTOBER, 31), HolidayType.OBSERVANCE))
        holidays.add(Holiday("Veterans Day", LocalDate.of(year, Month.NOVEMBER, 11), HolidayType.FEDERAL))
        holidays.add(Holiday("Christmas Day", LocalDate.of(year, Month.DECEMBER, 25), HolidayType.FEDERAL))
        holidays.add(Holiday("New Year's Eve", LocalDate.of(year, Month.DECEMBER, 31), HolidayType.OBSERVANCE))

        // Floating holidays (based on day of week)
        holidays.add(Holiday("Martin Luther King Jr. Day",
            getNthDayOfMonth(year, Month.JANUARY, DayOfWeek.MONDAY, 3), HolidayType.FEDERAL))
        holidays.add(Holiday("Presidents' Day",
            getNthDayOfMonth(year, Month.FEBRUARY, DayOfWeek.MONDAY, 3), HolidayType.FEDERAL))
        holidays.add(Holiday("Mother's Day",
            getNthDayOfMonth(year, Month.MAY, DayOfWeek.SUNDAY, 2), HolidayType.OBSERVANCE))
        holidays.add(Holiday("Memorial Day",
            getLastDayOfMonth(year, Month.MAY, DayOfWeek.MONDAY), HolidayType.FEDERAL))
        holidays.add(Holiday("Father's Day",
            getNthDayOfMonth(year, Month.JUNE, DayOfWeek.SUNDAY, 3), HolidayType.OBSERVANCE))
        holidays.add(Holiday("Labor Day",
            getNthDayOfMonth(year, Month.SEPTEMBER, DayOfWeek.MONDAY, 1), HolidayType.FEDERAL))
        holidays.add(Holiday("Columbus Day",
            getNthDayOfMonth(year, Month.OCTOBER, DayOfWeek.MONDAY, 2), HolidayType.FEDERAL))
        holidays.add(Holiday("Thanksgiving",
            getNthDayOfMonth(year, Month.NOVEMBER, DayOfWeek.THURSDAY, 4), HolidayType.FEDERAL))

        // Day after Thanksgiving
        val thanksgiving = getNthDayOfMonth(year, Month.NOVEMBER, DayOfWeek.THURSDAY, 4)
        holidays.add(Holiday("Black Friday", thanksgiving.plusDays(1), HolidayType.OBSERVANCE))

        return holidays.sortedBy { it.date }
    }

    /**
     * Get holiday for a specific date
     */
    fun getHoliday(date: LocalDate): Holiday? {
        val holidays = getHolidays(date.year)
        return holidays.find { it.date == date }
    }

    /**
     * Check if a date is a holiday
     */
    fun isHoliday(date: LocalDate): Boolean {
        return getHoliday(date) != null
    }

    /**
     * Get the nth occurrence of a day of week in a month
     * Example: 3rd Monday of January
     */
    private fun getNthDayOfMonth(year: Int, month: Month, dayOfWeek: DayOfWeek, n: Int): LocalDate {
        return LocalDate.of(year, month, 1)
            .with(TemporalAdjusters.dayOfWeekInMonth(n, dayOfWeek))
    }

    /**
     * Get the last occurrence of a day of week in a month
     * Example: Last Monday of May
     */
    private fun getLastDayOfMonth(year: Int, month: Month, dayOfWeek: DayOfWeek): LocalDate {
        return LocalDate.of(year, month, 1)
            .with(TemporalAdjusters.lastInMonth(dayOfWeek))
    }
}
