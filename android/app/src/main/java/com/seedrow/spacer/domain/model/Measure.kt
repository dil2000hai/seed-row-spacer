package com.seedrow.spacer.domain.model

import kotlin.math.abs
import kotlin.math.roundToInt

object Measure {

    fun value(valueCm: Int, unit: UnitSystem): String = when (unit) {
        UnitSystem.CM -> valueCm.toString()
        UnitSystem.INCH -> halfInches(valueCm)
    }

    fun area(areaM2: Float): String {
        val scaled = (areaM2 * 100f).roundToInt()
        return (scaled / 100).toString() + "." + pad(scaled % 100)
    }

    fun oneDecimal(value: Float): String {
        val scaled = (value * 10f).roundToInt()
        return (scaled / 10).toString() + "." + (scaled % 10).toString()
    }

    private fun halfInches(valueCm: Int): String {
        val halves = (valueCm / 2.54 * 2.0).roundToInt()
        val whole = halves / 2
        return if (halves % 2 == 0) whole.toString() else whole.toString() + ".5"
    }

    private fun pad(value: Int): String {
        val safe = abs(value)
        return if (safe < 10) "0" + safe.toString() else safe.toString()
    }
}
