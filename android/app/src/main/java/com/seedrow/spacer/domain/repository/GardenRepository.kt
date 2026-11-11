package com.seedrow.spacer.domain.repository

import com.seedrow.spacer.domain.model.Bed
import com.seedrow.spacer.domain.model.Crop
import com.seedrow.spacer.domain.model.GridKind
import com.seedrow.spacer.domain.model.PlanDraft
import com.seedrow.spacer.domain.model.PlantingPlan
import com.seedrow.spacer.domain.model.SeasonRecord
import kotlinx.coroutines.flow.StateFlow

interface GardenRepository {
    val beds: StateFlow<List<Bed>>
    val crops: StateFlow<List<Crop>>
    val draft: StateFlow<PlanDraft>
    val seasons: StateFlow<List<SeasonRecord>>
    val lastPlan: StateFlow<PlantingPlan?>
    val stripWidths: StateFlow<List<Int>>

    fun selectBed(bedId: String)
    fun setBedSize(lengthCm: Int, widthCm: Int, pathCm: Int)
    fun selectCrop(cropId: String)
    fun setSpacing(inRowCm: Int, betweenRowCm: Int)
    fun setSownEpochDay(epochDay: Long)
    fun setGridKind(kind: GridKind)
    fun setStripWidths(widths: List<Int>)
    fun savePlan(plan: PlantingPlan)
    fun saveSeason(record: SeasonRecord)
}
