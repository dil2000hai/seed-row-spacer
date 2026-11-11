package com.seedrow.spacer.presentation.strip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedrow.spacer.core.config.GameConfig
import com.seedrow.spacer.domain.repository.GardenRepository
import com.seedrow.spacer.domain.repository.SettingsRepository
import com.seedrow.spacer.domain.usecase.SplitBedIntoStripsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class StripPlannerViewModel(
    private val garden: GardenRepository,
    private val settings: SettingsRepository,
    private val splitBed: SplitBedIntoStripsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StripPlannerUiState())
    val state: StateFlow<StripPlannerUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                garden.draft,
                garden.crops,
                garden.stripWidths,
                settings.units
            ) { draft, crops, widths, units ->
                val picked = crops.take(GameConfig.STRIP_COUNT)
                val layout = splitBed(draft.bed, picked, widths, draft.gridKind)
                StripPlannerUiState(
                    bedName = draft.bed.name,
                    lengthCm = draft.bed.lengthCm,
                    widths = widths,
                    strips = layout.strips,
                    freeCm = layout.freeCm,
                    units = units
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }

    fun stepWidth(index: Int, direction: Int) {
        val widths = _state.value.widths
        if (index !in widths.indices) return
        val base = _state.value.strips.getOrNull(index)?.widthCm ?: widths[index]
        val updated = widths.toMutableList()
        updated[index] = (base + direction * GameConfig.SIZE_STEP_CM)
            .coerceIn(GameConfig.MIN_STRIP_CM, GameConfig.MAX_STRIP_CM)
        garden.setStripWidths(updated)
    }

    fun applyBestStrip() {
        val best = _state.value.strips.maxByOrNull { it.plants } ?: return
        if (best.plants <= 0) return
        garden.selectCrop(best.cropId)
    }
}
