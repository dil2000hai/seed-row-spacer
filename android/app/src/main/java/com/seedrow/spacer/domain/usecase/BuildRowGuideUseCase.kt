package com.seedrow.spacer.domain.usecase

import com.seedrow.spacer.domain.model.GridKind
import com.seedrow.spacer.domain.model.GridResult
import com.seedrow.spacer.domain.model.RowGuideEntry
import kotlin.math.max
import kotlin.math.roundToInt

class BuildRowGuideUseCase {

    operator fun invoke(
        grid: GridResult,
        inRowCm: Int,
        betweenRowCm: Int,
        kind: GridKind
    ): List<RowGuideEntry> {
        if (!grid.fits) return emptyList()
        val edge = inRowCm / 2.0
        val rowCount = grid.rowsFor(kind)
        val pitch = when (kind) {
            GridKind.SQUARE -> betweenRowCm.toDouble()
            GridKind.OFFSET -> betweenRowCm * CalculateGridUseCase.TRIANGULAR_PITCH
        }
        return (0 until rowCount).map { index ->
            val shifted = kind == GridKind.OFFSET && index % 2 == 1
            RowGuideEntry(
                index = index + 1,
                distanceCm = (edge + index * pitch).roundToInt(),
                plants = if (shifted) max(grid.columns - 1, 0) else grid.columns,
                shiftCm = if (shifted) inRowCm / 2f else 0f
            )
        }
    }
}
