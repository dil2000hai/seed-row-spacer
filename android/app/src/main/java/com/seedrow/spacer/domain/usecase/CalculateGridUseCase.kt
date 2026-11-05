package com.seedrow.spacer.domain.usecase

import com.seedrow.spacer.domain.model.GridResult
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

class CalculateGridUseCase {

    operator fun invoke(
        lengthCm: Int,
        widthCm: Int,
        inRowCm: Int,
        betweenRowCm: Int
    ): GridResult {
        if (inRowCm <= 0 || betweenRowCm <= 0) return GridResult.EMPTY
        if (lengthCm < inRowCm || widthCm < inRowCm) return GridResult.EMPTY

        val rows = floor((widthCm - inRowCm).toDouble() / betweenRowCm).toInt() + 1
        val columns = floor((lengthCm - inRowCm).toDouble() / inRowCm).toInt() + 1
        if (rows < 1 || columns < 1) return GridResult.EMPTY

        val square = rows * columns
        val pitch = betweenRowCm * TRIANGULAR_PITCH
        val offsetRows = floor((widthCm - inRowCm).toDouble() / pitch).toInt() + 1
        val oddColumns = max(columns - 1, 0)
        val fullRows = ceil(offsetRows / 2.0).toInt()
        val shiftedRows = floor(offsetRows / 2.0).toInt()
        val offset = fullRows * columns + shiftedRows * oddColumns
        val gain = ((offset - square) * 100.0 / max(square, 1)).roundToInt()

        return GridResult(
            rows = rows,
            columns = columns,
            offsetRows = offsetRows,
            squarePlants = square,
            offsetPlants = offset,
            gainPercent = gain
        )
    }

    companion object {
        const val TRIANGULAR_PITCH = 0.866
    }
}
