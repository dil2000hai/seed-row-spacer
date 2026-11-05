package com.seedrow.spacer.domain.model

data class Bed(
    val id: String,
    val name: String,
    val lengthCm: Int,
    val widthCm: Int,
    val pathCm: Int
) {
    val areaM2: Float
        get() = lengthCm.toFloat() * widthCm.toFloat() / 10000f
}
