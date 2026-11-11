package com.seedrow.spacer.presentation.strip

import com.seedrow.spacer.domain.model.Strip
import com.seedrow.spacer.domain.model.UnitSystem

data class StripPlannerUiState(
    val bedName: String = "",
    val lengthCm: Int = 0,
    val widths: List<Int> = emptyList(),
    val strips: List<Strip> = emptyList(),
    val freeCm: Int = 0,
    val units: UnitSystem = UnitSystem.CM
) {
    val totalPlants: Int
        get() = strips.sumOf { it.plants }
}
