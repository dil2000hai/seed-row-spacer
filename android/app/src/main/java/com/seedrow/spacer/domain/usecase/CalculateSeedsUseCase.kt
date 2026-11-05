package com.seedrow.spacer.domain.usecase

import com.seedrow.spacer.domain.model.SeedEstimate
import kotlin.math.ceil

class CalculateSeedsUseCase {

    operator fun invoke(totalPlants: Int, germinationPercent: Int): SeedEstimate {
        if (totalPlants <= 0) return SeedEstimate(0, 0)
        val germination = germinationPercent.coerceIn(1, 100)
        val allowance = ceil(totalPlants * 100.0 / germination * SOWING_LOSS).toInt()
        return SeedEstimate(seedsNeeded = totalPlants, seedsWithAllowance = allowance)
    }

    companion object {
        const val SOWING_LOSS = 1.1
    }
}
