package com.seedrow.spacer.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedrow.spacer.domain.model.GardenDates
import com.seedrow.spacer.domain.model.UnitSystem
import com.seedrow.spacer.domain.repository.GardenRepository
import com.seedrow.spacer.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val garden: GardenRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                garden.crops,
                settings.units,
                settings.defaultCropId,
                settings.frostEpochDay
            ) { crops, units, defaultCropId, frost ->
                SettingsUiState(
                    units = units,
                    crops = crops,
                    defaultCropId = defaultCropId,
                    frostEpochDay = frost,
                    frostText = GardenDates.format(frost)
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }

    fun selectUnits(units: UnitSystem) {
        if (units == _state.value.units) return
        settings.setUnits(units)
    }

    fun pickCrop(cropId: String) {
        settings.setDefaultCrop(cropId)
        garden.selectCrop(cropId)
    }

    fun stepFrost(direction: Int) {
        settings.setFrostEpochDay(_state.value.frostEpochDay + direction)
    }
}
