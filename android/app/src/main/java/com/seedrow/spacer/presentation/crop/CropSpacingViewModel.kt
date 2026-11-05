package com.seedrow.spacer.presentation.crop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedrow.spacer.core.config.GameConfig
import com.seedrow.spacer.domain.model.GardenDates
import com.seedrow.spacer.domain.repository.GardenRepository
import com.seedrow.spacer.domain.repository.SettingsRepository
import com.seedrow.spacer.domain.usecase.CalculateGridUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CropSpacingViewModel(
    private val garden: GardenRepository,
    private val settings: SettingsRepository,
    private val calculateGrid: CalculateGridUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CropSpacingUiState())
    val state: StateFlow<CropSpacingUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                garden.draft,
                garden.crops,
                settings.units
            ) { draft, crops, units ->
                val grid = calculateGrid(
                    lengthCm = draft.bed.lengthCm,
                    widthCm = draft.bed.widthCm,
                    inRowCm = draft.inRowCm,
                    betweenRowCm = draft.betweenRowCm
                )
                CropSpacingUiState(
                    crops = crops,
                    selectedCropId = draft.crop.id,
                    inRowCm = draft.inRowCm,
                    betweenRowCm = draft.betweenRowCm,
                    sownEpochDay = draft.sownEpochDay,
                    sownText = GardenDates.format(draft.sownEpochDay),
                    valid = grid.fits,
                    units = units
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }

    fun pickCrop(cropId: String) {
        garden.selectCrop(cropId)
    }

    fun stepInRow(direction: Int) {
        val current = _state.value
        val next = (current.inRowCm + direction * GameConfig.SPACING_STEP_CM)
            .coerceIn(GameConfig.MIN_IN_ROW_CM, GameConfig.MAX_IN_ROW_CM)
        garden.setSpacing(next, current.betweenRowCm)
    }

    fun stepBetweenRows(direction: Int) {
        val current = _state.value
        val next = (current.betweenRowCm + direction * GameConfig.SPACING_STEP_CM)
            .coerceIn(GameConfig.MIN_BETWEEN_ROW_CM, GameConfig.MAX_BETWEEN_ROW_CM)
        garden.setSpacing(current.inRowCm, next)
    }

    fun stepSowingDay(direction: Int) {
        garden.setSownEpochDay(_state.value.sownEpochDay + direction)
    }
}
