package com.seedrow.spacer.presentation.settings

import com.seedrow.spacer.domain.model.Crop
import com.seedrow.spacer.domain.model.UnitSystem

data class SettingsUiState(
    val units: UnitSystem = UnitSystem.CM,
    val crops: List<Crop> = emptyList(),
    val defaultCropId: String = "",
    val frostEpochDay: Long = 0L,
    val frostText: String = ""
)
