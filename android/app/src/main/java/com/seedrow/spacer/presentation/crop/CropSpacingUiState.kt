package com.seedrow.spacer.presentation.crop

import com.seedrow.spacer.domain.model.Crop
import com.seedrow.spacer.domain.model.UnitSystem

data class CropSpacingUiState(
    val crops: List<Crop> = emptyList(),
    val selectedCropId: String = "",
    val inRowCm: Int = 0,
    val betweenRowCm: Int = 0,
    val sownEpochDay: Long = 0L,
    val sownText: String = "",
    val valid: Boolean = false,
    val units: UnitSystem = UnitSystem.CM
)
