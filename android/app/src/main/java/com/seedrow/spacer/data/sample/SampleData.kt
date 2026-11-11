package com.seedrow.spacer.data.sample

import com.seedrow.spacer.domain.model.Bed
import com.seedrow.spacer.domain.model.Crop
import com.seedrow.spacer.domain.model.SeasonRecord

object SampleData {

    val crops: List<Crop> = listOf(
        Crop("radish", "RADISH", "BRASSICA", 5, 15, 92, 28, 1),
        Crop("carrot", "CARROT", "UMBELLIFER", 5, 25, 75, 75, 2),
        Crop("lettuce", "LETTUCE", "ASTER", 20, 30, 85, 55, 3),
        Crop("beetroot", "BEETROOT", "AMARANTH", 10, 30, 80, 60, 4),
        Crop("bushbean", "BUSH BEAN", "LEGUME", 12, 45, 80, 58, 5)
    )

    val beds: List<Bed> = listOf(
        Bed("south", "SOUTH BED", 240, 80, 40),
        Bed("greenhouse", "GREENHOUSE", 300, 90, 45),
        Bed("raised", "RAISED BOX", 120, 80, 30),
        Bed("herb", "HERB STRIP", 180, 45, 35)
    )

    val seasons: List<SeasonRecord> = listOf(
        SeasonRecord("south", 2024, "RADISH", "BRASSICA", 1),
        SeasonRecord("south", 2025, "CARROT", "UMBELLIFER", 2),
        SeasonRecord("greenhouse", 2024, "LETTUCE", "ASTER", 3),
        SeasonRecord("greenhouse", 2025, "LETTUCE", "ASTER", 3),
        SeasonRecord("raised", 2024, "BEETROOT", "AMARANTH", 4),
        SeasonRecord("raised", 2025, "BUSH BEAN", "LEGUME", 5),
        SeasonRecord("herb", 2024, "CARROT", "UMBELLIFER", 2),
        SeasonRecord("herb", 2025, "RADISH", "BRASSICA", 1)
    )

    val defaultCropId: String = crops.first().id

    val stripWidths: List<Int> = listOf(90, 80, 60)

    fun cropById(id: String): Crop = crops.firstOrNull { it.id == id } ?: crops.first()
}
