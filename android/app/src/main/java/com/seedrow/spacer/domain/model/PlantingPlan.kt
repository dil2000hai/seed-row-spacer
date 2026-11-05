package com.seedrow.spacer.domain.model

data class PlantingPlan(
    val bedId: String,
    val bedName: String,
    val cropId: String,
    val cropName: String,
    val cropFamily: String,
    val spriteIndex: Int,
    val gridKind: GridKind,
    val rows: Int,
    val columns: Int,
    val squarePlants: Int,
    val offsetPlants: Int,
    val totalPlants: Int,
    val gainPercent: Int,
    val germinationPercent: Int,
    val seedsNeeded: Int,
    val seedsWithAllowance: Int,
    val inRowCm: Int,
    val betweenRowCm: Int,
    val lengthCm: Int,
    val widthCm: Int,
    val sownEpochDay: Long,
    val harvestFromEpochDay: Long,
    val harvestToEpochDay: Long
) {
    val areaM2: Float
        get() = lengthCm.toFloat() * widthCm.toFloat() / 10000f
}
