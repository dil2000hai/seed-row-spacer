package com.seedrow.spacer.domain.usecase

import com.seedrow.spacer.domain.model.Bed
import com.seedrow.spacer.domain.model.Crop
import com.seedrow.spacer.domain.model.GridKind
import com.seedrow.spacer.domain.model.Strip
import com.seedrow.spacer.domain.model.StripLayout
import kotlin.math.max

class SplitBedIntoStripsUseCase(
    private val calculateGrid: CalculateGridUseCase
) {

    operator fun invoke(
        bed: Bed,
        crops: List<Crop>,
        widthsCm: List<Int>,
        kind: GridKind
    ): StripLayout {
        var remaining = bed.lengthCm
        val strips = crops.mapIndexed { index, crop ->
            val requested = widthsCm.getOrElse(index) { 0 }
            val granted = requested.coerceIn(0, max(remaining, 0))
            remaining -= granted
            val grid = calculateGrid(
                lengthCm = granted,
                widthCm = bed.widthCm,
                inRowCm = crop.inRowCm,
                betweenRowCm = crop.betweenRowCm
            )
            Strip(
                cropId = crop.id,
                cropName = crop.name,
                spriteIndex = crop.spriteIndex,
                widthCm = granted,
                plants = grid.plantsFor(kind)
            )
        }
        return StripLayout(strips = strips, freeCm = max(remaining, 0))
    }
}
