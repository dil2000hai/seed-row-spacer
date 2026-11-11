package com.seedrow.spacer.presentation.bedsize

import com.seedrow.spacer.domain.model.UnitSystem

data class BedSizeUiState(
    val bedName: String = "",
    val lengthCm: Int = 0,
    val widthCm: Int = 0,
    val pathCm: Int = 0,
    val areaM2: Float = 0f,
    val inRowCm: Int = 0,
    val valid: Boolean = false,
    val units: UnitSystem = UnitSystem.CM
)
