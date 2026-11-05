package com.seedrow.spacer.data.repository

import com.seedrow.spacer.data.local.SettingsPreferences
import com.seedrow.spacer.data.sample.SampleData
import com.seedrow.spacer.domain.model.GardenDates
import com.seedrow.spacer.domain.model.UnitSystem
import com.seedrow.spacer.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val storage: SettingsPreferences
) : SettingsRepository {

    private val _units = MutableStateFlow(loadUnits())
    override val units: StateFlow<UnitSystem> = _units.asStateFlow()

    private val _defaultCropId = MutableStateFlow(
        storage.readString(SettingsPreferences.KEY_DEFAULT_CROP, SampleData.defaultCropId)
    )
    override val defaultCropId: StateFlow<String> = _defaultCropId.asStateFlow()

    private val _frostEpochDay = MutableStateFlow(
        storage.readLong(SettingsPreferences.KEY_FROST, GardenDates.today())
    )
    override val frostEpochDay: StateFlow<Long> = _frostEpochDay.asStateFlow()

    override fun setUnits(units: UnitSystem) {
        _units.value = units
        storage.writeString(SettingsPreferences.KEY_UNITS, units.name)
    }

    override fun setDefaultCrop(cropId: String) {
        _defaultCropId.value = cropId
        storage.writeString(SettingsPreferences.KEY_DEFAULT_CROP, cropId)
    }

    override fun setFrostEpochDay(epochDay: Long) {
        _frostEpochDay.value = epochDay
        storage.writeLong(SettingsPreferences.KEY_FROST, epochDay)
    }

    private fun loadUnits(): UnitSystem {
        val stored = storage.readString(SettingsPreferences.KEY_UNITS, UnitSystem.CM.name)
        return UnitSystem.entries.firstOrNull { it.name == stored } ?: UnitSystem.CM
    }
}
