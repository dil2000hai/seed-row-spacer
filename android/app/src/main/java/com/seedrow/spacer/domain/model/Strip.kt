package com.seedrow.spacer.domain.model

data class Strip(
    val cropId: String,
    val cropName: String,
    val spriteIndex: Int,
    val widthCm: Int,
    val plants: Int
)

data class StripLayout(
    val strips: List<Strip>,
    val freeCm: Int
)
