package com.seedrow.spacer.domain.model

data class Crop(
    val id: String,
    val name: String,
    val family: String,
    val inRowCm: Int,
    val betweenRowCm: Int,
    val germinationPercent: Int,
    val daysToHarvest: Int,
    val spriteIndex: Int
)
