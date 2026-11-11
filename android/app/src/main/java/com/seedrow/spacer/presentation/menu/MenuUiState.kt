package com.seedrow.spacer.presentation.menu

import com.seedrow.spacer.domain.model.Bed
import com.seedrow.spacer.domain.model.Crop
import com.seedrow.spacer.domain.model.UnitSystem

data class MenuUiState(
    val season: Int = 0,
    val bedCount: Int = 0,
    val cropCount: Int = 0,
    val plantsInSelectedBed: Int = 0,
    val selectedBedId: String = "",
    val recentBeds: List<Bed> = emptyList(),
    val crops: List<Crop> = emptyList(),
    val units: UnitSystem = UnitSystem.CM
)
