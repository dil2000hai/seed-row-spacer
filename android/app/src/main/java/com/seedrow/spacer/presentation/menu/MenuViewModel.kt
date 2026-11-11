package com.seedrow.spacer.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedrow.spacer.domain.model.GardenDates
import com.seedrow.spacer.domain.repository.GardenRepository
import com.seedrow.spacer.domain.repository.SettingsRepository
import com.seedrow.spacer.domain.usecase.CalculateGridUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MenuViewModel(
    private val garden: GardenRepository,
    private val settings: SettingsRepository,
    private val calculateGrid: CalculateGridUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                garden.beds,
                garden.crops,
                garden.draft,
                settings.units
            ) { beds, crops, draft, units ->
                val grid = calculateGrid(
                    lengthCm = draft.bed.lengthCm,
                    widthCm = draft.bed.widthCm,
                    inRowCm = draft.inRowCm,
                    betweenRowCm = draft.betweenRowCm
                )
                MenuUiState(
                    season = GardenDates.yearOf(draft.sownEpochDay),
                    bedCount = beds.size,
                    cropCount = crops.size,
                    plantsInSelectedBed = grid.plantsFor(draft.gridKind),
                    selectedBedId = draft.bed.id,
                    recentBeds = beds.take(RECENT_BEDS),
                    crops = crops,
                    units = units
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }

    fun pickBed(bedId: String) {
        garden.selectBed(bedId)
    }

    private companion object {
        const val RECENT_BEDS = 2
    }
}
