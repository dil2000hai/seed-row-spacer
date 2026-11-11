package com.seedrow.spacer.domain.model

import java.time.LocalDate

object GardenDates {

    private val MONTHS = listOf(
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    )

    fun today(): Long = LocalDate.now().toEpochDay()

    fun yearOf(epochDay: Long): Int = LocalDate.ofEpochDay(epochDay).year

    fun format(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        val month = MONTHS[date.monthValue - 1]
        return date.dayOfMonth.toString() + " " + month + " " + date.year
    }

    fun formatShort(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        return date.dayOfMonth.toString() + " " + MONTHS[date.monthValue - 1]
    }
}
