package com.seedrow.spacer.presentation.game

import com.seedrow.spacer.domain.model.GridKind
import com.seedrow.spacer.domain.model.UnitSystem

data class GameUiState(
    val bedName: String = "",
    val cropName: String = "",
    val spriteIndex: Int = 1,
    val lengthCm: Int = 0,
    val widthCm: Int = 0,
    val inRowCm: Int = 0,
    val betweenRowCm: Int = 0,
    val gridKind: GridKind = GridKind.SQUARE,
    val rows: Int = 0,
    val columns: Int = 0,
    val squarePlants: Int = 0,
    val offsetPlants: Int = 0,
    val gainPercent: Int = 0,
    val revealedRows: Int = 0,
    val paused: Boolean = false,
    val fits: Boolean = false,
    val units: UnitSystem = UnitSystem.CM
) {
    val totalPlants: Int
        get() = when (gridKind) {
            GridKind.SQUARE -> squarePlants
            GridKind.OFFSET -> offsetPlants
        }

    val rowPitchCm: Float
        get() = when (gridKind) {
            GridKind.SQUARE -> betweenRowCm.toFloat()
            GridKind.OFFSET -> betweenRowCm * 0.866f
        }
}
