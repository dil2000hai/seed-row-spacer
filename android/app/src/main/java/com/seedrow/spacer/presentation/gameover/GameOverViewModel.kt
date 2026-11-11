package com.seedrow.spacer.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedrow.spacer.domain.model.GardenDates
import com.seedrow.spacer.domain.model.PlantingPlan
import com.seedrow.spacer.domain.model.SeasonRecord
import com.seedrow.spacer.domain.model.UnitSystem
import com.seedrow.spacer.domain.repository.GardenRepository
import com.seedrow.spacer.domain.repository.SettingsRepository
import com.seedrow.spacer.domain.usecase.CalculateGridUseCase
import com.seedrow.spacer.domain.usecase.CalculateSeedsUseCase
import com.seedrow.spacer.domain.usecase.EstimateHarvestUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameOverViewModel(
    private val garden: GardenRepository,
    private val settings: SettingsRepository,
    private val calculateGrid: CalculateGridUseCase,
    private val calculateSeeds: CalculateSeedsUseCase,
    private val estimateHarvest: EstimateHarvestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private var plan: PlantingPlan? = null

    init {
        viewModelScope.launch {
            combine(
                garden.draft,
                settings.units,
                settings.frostEpochDay
            ) { draft, units, frost ->
                val grid = calculateGrid(
                    lengthCm = draft.bed.lengthCm,
                    widthCm = draft.bed.widthCm,
                    inRowCm = draft.inRowCm,
                    betweenRowCm = draft.betweenRowCm
                )
                val total = grid.plantsFor(draft.gridKind)
                val seeds = calculateSeeds(total, draft.crop.germinationPercent)
                val harvest = estimateHarvest(
                    sownEpochDay = draft.sownEpochDay,
                    daysToHarvest = draft.crop.daysToHarvest,
                    frostEpochDay = frost
                )
                val built = PlantingPlan(
                    bedId = draft.bed.id,
                    bedName = draft.bed.name,
                    cropId = draft.crop.id,
                    cropName = draft.crop.name,
                    cropFamily = draft.crop.family,
                    spriteIndex = draft.crop.spriteIndex,
                    gridKind = draft.gridKind,
                    rows = grid.rowsFor(draft.gridKind),
                    columns = grid.columns,
                    squarePlants = grid.squarePlants,
                    offsetPlants = grid.offsetPlants,
                    totalPlants = total,
                    gainPercent = grid.gainPercent,
                    germinationPercent = draft.crop.germinationPercent,
                    seedsNeeded = seeds.seedsNeeded,
                    seedsWithAllowance = seeds.seedsWithAllowance,
                    inRowCm = draft.inRowCm,
                    betweenRowCm = draft.betweenRowCm,
                    lengthCm = draft.bed.lengthCm,
                    widthCm = draft.bed.widthCm,
                    sownEpochDay = harvest.sownEpochDay,
                    harvestFromEpochDay = harvest.fromEpochDay,
                    harvestToEpochDay = harvest.toEpochDay
                )
                Pair(built, units)
            }.collect { pair ->
                plan = pair.first
                _state.update { current -> toUiState(pair.first, pair.second, current.savedToRotation) }
            }
        }
    }

    fun onResult(totalPlants: Int) {
        val current = plan ?: return
        val resolved = if (totalPlants > 0 && totalPlants != current.totalPlants) {
            val seeds = calculateSeeds(totalPlants, current.germinationPercent)
            current.copy(
                totalPlants = totalPlants,
                seedsNeeded = seeds.seedsNeeded,
                seedsWithAllowance = seeds.seedsWithAllowance
            )
        } else {
            current
        }
        plan = resolved
        garden.savePlan(resolved)
        _state.update { toUiState(resolved, it.units, it.savedToRotation) }
    }

    fun saveToRotation() {
        val current = plan ?: return
        garden.saveSeason(
            SeasonRecord(
                bedId = current.bedId,
                year = GardenDates.yearOf(current.sownEpochDay),
                cropName = current.cropName,
                cropFamily = current.cropFamily,
                spriteIndex = current.spriteIndex
            )
        )
        _state.update { it.copy(savedToRotation = true) }
    }

    private fun toUiState(
        source: PlantingPlan,
        units: UnitSystem,
        saved: Boolean
    ): GameOverUiState = GameOverUiState(
        bedName = source.bedName,
        cropName = source.cropName,
        spriteIndex = source.spriteIndex,
        gridKind = source.gridKind,
        totalPlants = source.totalPlants,
        gainPercent = source.gainPercent,
        germinationPercent = source.germinationPercent,
        seedsNeeded = source.seedsNeeded,
        seedsWithAllowance = source.seedsWithAllowance,
        inRowCm = source.inRowCm,
        betweenRowCm = source.betweenRowCm,
        areaM2 = source.areaM2,
        sownText = GardenDates.format(source.sownEpochDay),
        harvestFromText = GardenDates.formatShort(source.harvestFromEpochDay),
        harvestToText = GardenDates.format(source.harvestToEpochDay),
        savedToRotation = saved,
        units = units
    )
}
