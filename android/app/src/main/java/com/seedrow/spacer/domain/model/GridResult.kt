package com.seedrow.spacer.domain.model

data class GridResult(
    val rows: Int,
    val columns: Int,
    val offsetRows: Int,
    val squarePlants: Int,
    val offsetPlants: Int,
    val gainPercent: Int
) {
    val fits: Boolean
        get() = squarePlants > 0

    fun plantsFor(kind: GridKind): Int = when (kind) {
        GridKind.SQUARE -> squarePlants
        GridKind.OFFSET -> offsetPlants
    }

    fun rowsFor(kind: GridKind): Int = when (kind) {
        GridKind.SQUARE -> rows
        GridKind.OFFSET -> offsetRows
    }

    companion object {
        val EMPTY = GridResult(0, 0, 0, 0, 0, 0)
    }
}
