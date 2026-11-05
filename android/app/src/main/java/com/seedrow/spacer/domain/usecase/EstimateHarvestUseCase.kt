package com.seedrow.spacer.domain.usecase

import com.seedrow.spacer.domain.model.HarvestWindow
import kotlin.math.ceil
import kotlin.math.max

class EstimateHarvestUseCase {

    operator fun invoke(
        sownEpochDay: Long,
        daysToHarvest: Int,
        frostEpochDay: Long
    ): HarvestWindow {
        val sown = max(sownEpochDay, frostEpochDay)
        val from = sown + daysToHarvest
        val picking = ceil(daysToHarvest * PICKING_SHARE).toLong()
        return HarvestWindow(
            sownEpochDay = sown,
            fromEpochDay = from,
            toEpochDay = from + picking
        )
    }

    companion object {
        const val PICKING_SHARE = 0.18
    }
}
