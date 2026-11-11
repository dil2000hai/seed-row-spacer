package com.seedrow.spacer.domain.repository

import com.seedrow.spacer.domain.model.UnitSystem
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val units: StateFlow<UnitSystem>
    val defaultCropId: StateFlow<String>
    val frostEpochDay: StateFlow<Long>

    fun setUnits(units: UnitSystem)
    fun setDefaultCrop(cropId: String)
    fun setFrostEpochDay(epochDay: Long)
}
