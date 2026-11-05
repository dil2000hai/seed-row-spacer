package com.seedrow.spacer.domain.model

data class HarvestWindow(
    val sownEpochDay: Long,
    val fromEpochDay: Long,
    val toEpochDay: Long
) {
    val growingDays: Int
        get() = (fromEpochDay - sownEpochDay).toInt()

    val pickingDays: Int
        get() = (toEpochDay - fromEpochDay).toInt()
}
