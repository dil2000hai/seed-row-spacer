package com.seedrow.spacer.data.repository

import com.seedrow.spacer.data.local.GardenPreferences
import com.seedrow.spacer.data.sample.SampleData
import com.seedrow.spacer.domain.model.Bed
import com.seedrow.spacer.domain.model.Crop
import com.seedrow.spacer.domain.model.GardenDates
import com.seedrow.spacer.domain.model.GridKind
import com.seedrow.spacer.domain.model.PlanDraft
import com.seedrow.spacer.domain.model.PlantingPlan
import com.seedrow.spacer.domain.model.SeasonRecord
import com.seedrow.spacer.domain.repository.GardenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GardenRepositoryImpl(
    private val storage: GardenPreferences
) : GardenRepository {

    private val _beds = MutableStateFlow(loadBeds())
    override val beds: StateFlow<List<Bed>> = _beds.asStateFlow()

    private val _crops = MutableStateFlow(SampleData.crops)
    override val crops: StateFlow<List<Crop>> = _crops.asStateFlow()

    private val _seasons = MutableStateFlow(loadSeasons())
    override val seasons: StateFlow<List<SeasonRecord>> = _seasons.asStateFlow()

    private val _stripWidths = MutableStateFlow(loadStripWidths())
    override val stripWidths: StateFlow<List<Int>> = _stripWidths.asStateFlow()

    private val _lastPlan = MutableStateFlow(loadLastPlan())
    override val lastPlan: StateFlow<PlantingPlan?> = _lastPlan.asStateFlow()

    private val _draft = MutableStateFlow(loadDraft())
    override val draft: StateFlow<PlanDraft> = _draft.asStateFlow()

    override fun selectBed(bedId: String) {
        val bed = _beds.value.firstOrNull { it.id == bedId } ?: return
        storage.writeString(GardenPreferences.KEY_SELECTED_BED, bed.id)
        _draft.update { it.copy(bed = bed) }
    }

    override fun setBedSize(lengthCm: Int, widthCm: Int, pathCm: Int) {
        val current = _draft.value.bed
        val updated = current.copy(lengthCm = lengthCm, widthCm = widthCm, pathCm = pathCm)
        _beds.update { list -> list.map { if (it.id == updated.id) updated else it } }
        storage.writeString(GardenPreferences.KEY_BEDS, encodeBeds(_beds.value))
        _draft.update { it.copy(bed = updated) }
    }

    override fun selectCrop(cropId: String) {
        val crop = _crops.value.firstOrNull { it.id == cropId } ?: return
        storage.writeString(GardenPreferences.KEY_CROP, crop.id)
        storage.writeInt(GardenPreferences.KEY_IN_ROW, crop.inRowCm)
        storage.writeInt(GardenPreferences.KEY_BETWEEN_ROW, crop.betweenRowCm)
        _draft.update {
            it.copy(crop = crop, inRowCm = crop.inRowCm, betweenRowCm = crop.betweenRowCm)
        }
    }

    override fun setSpacing(inRowCm: Int, betweenRowCm: Int) {
        storage.writeInt(GardenPreferences.KEY_IN_ROW, inRowCm)
        storage.writeInt(GardenPreferences.KEY_BETWEEN_ROW, betweenRowCm)
        _draft.update { it.copy(inRowCm = inRowCm, betweenRowCm = betweenRowCm) }
    }

    override fun setSownEpochDay(epochDay: Long) {
        storage.writeLong(GardenPreferences.KEY_SOWN, epochDay)
        _draft.update { it.copy(sownEpochDay = epochDay) }
    }

    override fun setGridKind(kind: GridKind) {
        storage.writeString(GardenPreferences.KEY_GRID_KIND, kind.name)
        _draft.update { it.copy(gridKind = kind) }
    }

    override fun setStripWidths(widths: List<Int>) {
        _stripWidths.value = widths
        storage.writeString(GardenPreferences.KEY_STRIPS, widths.joinToString(FIELD))
    }

    override fun savePlan(plan: PlantingPlan) {
        _lastPlan.value = plan
        storage.writeString(GardenPreferences.KEY_LAST_PLAN, encodePlan(plan))
    }

    override fun saveSeason(record: SeasonRecord) {
        val merged = _seasons.value
            .filterNot { it.bedId == record.bedId && it.year == record.year }
            .plus(record)
            .sortedWith(compareBy({ it.bedId }, { it.year }))
        _seasons.value = merged
        storage.writeString(GardenPreferences.KEY_SEASONS, encodeSeasons(merged))
    }

    private fun loadBeds(): List<Bed> {
        val raw = storage.readString(GardenPreferences.KEY_BEDS, "")
        if (raw.isBlank()) return SampleData.beds
        val parsed = raw.split(RECORD).mapNotNull { decodeBed(it) }
        return parsed.ifEmpty { SampleData.beds }
    }

    private fun loadSeasons(): List<SeasonRecord> {
        val raw = storage.readString(GardenPreferences.KEY_SEASONS, "")
        if (raw.isBlank()) return SampleData.seasons
        val parsed = raw.split(RECORD).mapNotNull { decodeSeason(it) }
        return parsed.ifEmpty { SampleData.seasons }
    }

    private fun loadStripWidths(): List<Int> {
        val raw = storage.readString(GardenPreferences.KEY_STRIPS, "")
        if (raw.isBlank()) return SampleData.stripWidths
        val parsed = raw.split(FIELD).mapNotNull { it.toIntOrNull() }
        return if (parsed.size == SampleData.stripWidths.size) parsed else SampleData.stripWidths
    }

    private fun loadDraft(): PlanDraft {
        val bedId = storage.readString(GardenPreferences.KEY_SELECTED_BED, "")
        val bed = _beds.value.firstOrNull { it.id == bedId } ?: _beds.value.first()
        val crop = SampleData.cropById(
            storage.readString(GardenPreferences.KEY_CROP, SampleData.defaultCropId)
        )
        val kindName = storage.readString(GardenPreferences.KEY_GRID_KIND, GridKind.SQUARE.name)
        return PlanDraft(
            bed = bed,
            crop = crop,
            inRowCm = storage.readInt(GardenPreferences.KEY_IN_ROW, crop.inRowCm),
            betweenRowCm = storage.readInt(GardenPreferences.KEY_BETWEEN_ROW, crop.betweenRowCm),
            gridKind = GridKind.entries.firstOrNull { it.name == kindName } ?: GridKind.SQUARE,
            sownEpochDay = storage.readLong(GardenPreferences.KEY_SOWN, GardenDates.today())
        )
    }

    private fun loadLastPlan(): PlantingPlan? {
        val raw = storage.readString(GardenPreferences.KEY_LAST_PLAN, "")
        if (raw.isBlank()) return null
        val parts = raw.split(FIELD)
        if (parts.size < PLAN_FIELDS) return null
        return runCatching {
            PlantingPlan(
                bedId = parts[0],
                bedName = parts[1],
                cropId = parts[2],
                cropName = parts[3],
                cropFamily = parts[4],
                spriteIndex = parts[5].toInt(),
                gridKind = GridKind.valueOf(parts[6]),
                rows = parts[7].toInt(),
                columns = parts[8].toInt(),
                squarePlants = parts[9].toInt(),
                offsetPlants = parts[10].toInt(),
                totalPlants = parts[11].toInt(),
                gainPercent = parts[12].toInt(),
                germinationPercent = parts[13].toInt(),
                seedsNeeded = parts[14].toInt(),
                seedsWithAllowance = parts[15].toInt(),
                inRowCm = parts[16].toInt(),
                betweenRowCm = parts[17].toInt(),
                lengthCm = parts[18].toInt(),
                widthCm = parts[19].toInt(),
                sownEpochDay = parts[20].toLong(),
                harvestFromEpochDay = parts[21].toLong(),
                harvestToEpochDay = parts[22].toLong()
            )
        }.getOrNull()
    }

    private fun encodePlan(plan: PlantingPlan): String = listOf(
        plan.bedId,
        plan.bedName,
        plan.cropId,
        plan.cropName,
        plan.cropFamily,
        plan.spriteIndex.toString(),
        plan.gridKind.name,
        plan.rows.toString(),
        plan.columns.toString(),
        plan.squarePlants.toString(),
        plan.offsetPlants.toString(),
        plan.totalPlants.toString(),
        plan.gainPercent.toString(),
        plan.germinationPercent.toString(),
        plan.seedsNeeded.toString(),
        plan.seedsWithAllowance.toString(),
        plan.inRowCm.toString(),
        plan.betweenRowCm.toString(),
        plan.lengthCm.toString(),
        plan.widthCm.toString(),
        plan.sownEpochDay.toString(),
        plan.harvestFromEpochDay.toString(),
        plan.harvestToEpochDay.toString()
    ).joinToString(FIELD)

    private fun encodeBeds(list: List<Bed>): String = list.joinToString(RECORD) { bed ->
        listOf(
            bed.id,
            bed.name,
            bed.lengthCm.toString(),
            bed.widthCm.toString(),
            bed.pathCm.toString()
        ).joinToString(FIELD)
    }

    private fun decodeBed(raw: String): Bed? {
        val parts = raw.split(FIELD)
        if (parts.size < 5) return null
        return runCatching {
            Bed(parts[0], parts[1], parts[2].toInt(), parts[3].toInt(), parts[4].toInt())
        }.getOrNull()
    }

    private fun encodeSeasons(list: List<SeasonRecord>): String = list.joinToString(RECORD) { item ->
        listOf(
            item.bedId,
            item.year.toString(),
            item.cropName,
            item.cropFamily,
            item.spriteIndex.toString()
        ).joinToString(FIELD)
    }

    private fun decodeSeason(raw: String): SeasonRecord? {
        val parts = raw.split(FIELD)
        if (parts.size < 5) return null
        return runCatching {
            SeasonRecord(parts[0], parts[1].toInt(), parts[2], parts[3], parts[4].toInt())
        }.getOrNull()
    }

    private companion object {
        const val FIELD = "|"
        const val RECORD = ";"
        const val PLAN_FIELDS = 23
    }
}
