package com.seedrow.spacer.presentation.bedsize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedrow.spacer.core.config.GameConfig
import com.seedrow.spacer.domain.repository.GardenRepository
import com.seedrow.spacer.domain.repository.SettingsRepository
import com.seedrow.spacer.domain.usecase.CalculateGridUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BedSizeViewModel(
    private val garden: GardenRepository,
    private val settings: SettingsRepository,
    private val calculateGrid: CalculateGridUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BedSizeUiState())
    val state: StateFlow<BedSizeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(garden.draft, settings.units) { draft, units ->
                val grid = calculateGrid(
                    lengthCm = draft.bed.lengthCm,
                    widthCm = draft.bed.widthCm,
                    inRowCm = draft.inRowCm,
                    betweenRowCm = draft.betweenRowCm
                )
                BedSizeUiState(
                    bedName = draft.bed.name,
                    lengthCm = draft.bed.lengthCm,
                    widthCm = draft.bed.widthCm,
                    pathCm = draft.bed.pathCm,
                    areaM2 = draft.bed.areaM2,
                    inRowCm = draft.inRowCm,
                    valid = grid.fits,
                    units = units
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }

    fun stepLength(direction: Int) {
        val current = _state.value
        val next = (current.lengthCm + direction * GameConfig.SIZE_STEP_CM)
            .coerceIn(GameConfig.MIN_LENGTH_CM, GameConfig.MAX_LENGTH_CM)
        garden.setBedSize(next, current.widthCm, current.pathCm)
    }

    fun stepWidth(direction: Int) {
        val current = _state.value
        val next = (current.widthCm + direction * GameConfig.SIZE_STEP_CM)
            .coerceIn(GameConfig.MIN_WIDTH_CM, GameConfig.MAX_WIDTH_CM)
        garden.setBedSize(current.lengthCm, next, current.pathCm)
    }

    fun stepPath(direction: Int) {
        val current = _state.value
        val next = (current.pathCm + direction * GameConfig.SIZE_STEP_CM)
            .coerceIn(GameConfig.MIN_PATH_CM, GameConfig.MAX_PATH_CM)
        garden.setBedSize(current.lengthCm, current.widthCm, next)
    }
}
