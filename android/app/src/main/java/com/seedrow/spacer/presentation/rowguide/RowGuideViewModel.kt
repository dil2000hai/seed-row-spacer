package com.seedrow.spacer.presentation.rowguide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedrow.spacer.core.config.GameConfig
import com.seedrow.spacer.domain.repository.GardenRepository
import com.seedrow.spacer.domain.repository.SettingsRepository
import com.seedrow.spacer.domain.usecase.BuildRowGuideUseCase
import com.seedrow.spacer.domain.usecase.CalculateGridUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RowGuideViewModel(
    private val garden: GardenRepository,
    private val settings: SettingsRepository,
    private val calculateGrid: CalculateGridUseCase,
    private val buildRowGuide: BuildRowGuideUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RowGuideUiState())
    val state: StateFlow<RowGuideUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(garden.draft, settings.units) { draft, units ->
                val grid = calculateGrid(
                    lengthCm = draft.bed.lengthCm,
                    widthCm = draft.bed.widthCm,
                    inRowCm = draft.inRowCm,
                    betweenRowCm = draft.betweenRowCm
                )
                val all = buildRowGuide(grid, draft.inRowCm, draft.betweenRowCm, draft.gridKind)
                val shown = all.take(GameConfig.ROW_GUIDE_LIMIT)
                RowGuideUiState(
                    cropName = draft.crop.name,
                    spriteIndex = draft.crop.spriteIndex,
                    entries = shown,
                    hiddenFrom = if (all.size > shown.size) shown.size + 1 else 0,
                    hiddenTo = if (all.size > shown.size) all.size else 0,
                    units = units
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }
}
