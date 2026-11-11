package com.seedrow.spacer.presentation.rowguide

import com.seedrow.spacer.domain.model.RowGuideEntry
import com.seedrow.spacer.domain.model.UnitSystem

data class RowGuideUiState(
    val cropName: String = "",
    val spriteIndex: Int = 1,
    val entries: List<RowGuideEntry> = emptyList(),
    val hiddenFrom: Int = 0,
    val hiddenTo: Int = 0,
    val units: UnitSystem = UnitSystem.CM
)
