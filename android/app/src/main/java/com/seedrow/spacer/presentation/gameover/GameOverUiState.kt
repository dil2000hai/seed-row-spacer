package com.seedrow.spacer.presentation.gameover

import com.seedrow.spacer.domain.model.GridKind
import com.seedrow.spacer.domain.model.UnitSystem

data class GameOverUiState(
    val bedName: String = "",
    val cropName: String = "",
    val spriteIndex: Int = 1,
    val gridKind: GridKind = GridKind.SQUARE,
    val totalPlants: Int = 0,
    val gainPercent: Int = 0,
    val germinationPercent: Int = 0,
    val seedsNeeded: Int = 0,
    val seedsWithAllowance: Int = 0,
    val inRowCm: Int = 0,
    val betweenRowCm: Int = 0,
    val areaM2: Float = 0f,
    val sownText: String = "",
    val harvestFromText: String = "",
    val harvestToText: String = "",
    val savedToRotation: Boolean = false,
    val units: UnitSystem = UnitSystem.CM
)
