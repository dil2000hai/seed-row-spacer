package com.seedrow.spacer.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.seedrow.spacer.domain.repository.GardenRepository
import com.seedrow.spacer.domain.repository.SettingsRepository
import com.seedrow.spacer.domain.usecase.BuildRowGuideUseCase
import com.seedrow.spacer.domain.usecase.CalculateGridUseCase
import com.seedrow.spacer.domain.usecase.CalculateSeedsUseCase
import com.seedrow.spacer.domain.usecase.EstimateHarvestUseCase
import com.seedrow.spacer.domain.usecase.SplitBedIntoStripsUseCase
import com.seedrow.spacer.presentation.app.AppViewModel
import com.seedrow.spacer.presentation.bedsize.BedSizeViewModel
import com.seedrow.spacer.presentation.crop.CropSpacingViewModel
import com.seedrow.spacer.presentation.game.GameViewModel
import com.seedrow.spacer.presentation.gameover.GameOverViewModel
import com.seedrow.spacer.presentation.menu.MenuViewModel
import com.seedrow.spacer.presentation.rotation.RotationNotesViewModel
import com.seedrow.spacer.presentation.rowguide.RowGuideViewModel
import com.seedrow.spacer.presentation.settings.SettingsViewModel
import com.seedrow.spacer.presentation.splash.SplashViewModel
import com.seedrow.spacer.presentation.strip.StripPlannerViewModel

object AppViewModelFactory {

    fun create(
        garden: GardenRepository,
        settings: SettingsRepository
    ): ViewModelProvider.Factory {
        val calculateGrid = CalculateGridUseCase()
        val calculateSeeds = CalculateSeedsUseCase()
        val estimateHarvest = EstimateHarvestUseCase()
        val buildRowGuide = BuildRowGuideUseCase()
        val splitBed = SplitBedIntoStripsUseCase(calculateGrid)

        return viewModelFactory {
            initializer { AppViewModel() }
            initializer { SplashViewModel() }
            initializer { MenuViewModel(garden, settings, calculateGrid) }
            initializer { BedSizeViewModel(garden, settings, calculateGrid) }
            initializer { CropSpacingViewModel(garden, settings, calculateGrid) }
            initializer { GameViewModel(garden, settings, calculateGrid) }
            initializer { RowGuideViewModel(garden, settings, calculateGrid, buildRowGuide) }
            initializer {
                GameOverViewModel(
                    garden,
                    settings,
                    calculateGrid,
                    calculateSeeds,
                    estimateHarvest
                )
            }
            initializer { StripPlannerViewModel(garden, settings, splitBed) }
            initializer { RotationNotesViewModel(garden) }
            initializer { SettingsViewModel(garden, settings) }
        }
    }
}
