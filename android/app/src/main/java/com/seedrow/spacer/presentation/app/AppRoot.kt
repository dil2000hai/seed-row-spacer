package com.seedrow.spacer.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedrow.spacer.core.di.ServiceLocator
import com.seedrow.spacer.core.navigation.Screen
import com.seedrow.spacer.core.theme.PaperBg
import com.seedrow.spacer.presentation.bedsize.BedSizeRoute
import com.seedrow.spacer.presentation.crop.CropSpacingRoute
import com.seedrow.spacer.presentation.game.GameRoute
import com.seedrow.spacer.presentation.gameover.GameOverRoute
import com.seedrow.spacer.presentation.menu.MenuRoute
import com.seedrow.spacer.presentation.rotation.RotationNotesRoute
import com.seedrow.spacer.presentation.rowguide.RowGuideRoute
import com.seedrow.spacer.presentation.settings.SettingsRoute
import com.seedrow.spacer.presentation.splash.SplashRoute
import com.seedrow.spacer.presentation.strip.StripPlannerRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()

    BackHandler(enabled = screen !is Screen.Menu && screen !is Screen.Splash) {
        appViewModel.back()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBg)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                if (initialState is Screen.Splash) {
                    fadeIn(tween(420)) togetherWith fadeOut(tween(320))
                } else {
                    (slideInHorizontally(tween(300)) { full -> full / 6 } + fadeIn(tween(300)))
                        .togetherWith(fadeOut(tween(220)))
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { target ->
            when (target) {
                is Screen.Splash -> SplashRoute(
                    onFinished = { appViewModel.enterMenu() }
                )

                is Screen.Menu -> MenuRoute(
                    onStart = { appViewModel.navigate(Screen.BedSize) },
                    onStrips = { appViewModel.navigate(Screen.StripPlanner) },
                    onRotation = { appViewModel.navigate(Screen.RotationNotes) },
                    onRowGuide = { appViewModel.navigate(Screen.RowGuide) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                is Screen.BedSize -> BedSizeRoute(
                    onHome = { appViewModel.enterMenu() },
                    onNext = { appViewModel.navigate(Screen.CropSpacing) }
                )

                is Screen.CropSpacing -> CropSpacingRoute(
                    onHome = { appViewModel.enterMenu() },
                    onBack = { appViewModel.back() },
                    onShowGrid = { appViewModel.navigate(Screen.Game) }
                )

                is Screen.Game -> GameRoute(
                    onHome = { appViewModel.enterMenu() },
                    onRowGuide = { appViewModel.navigate(Screen.RowGuide) },
                    onResult = { plants -> appViewModel.navigate(Screen.GameOver(plants)) }
                )

                is Screen.RowGuide -> RowGuideRoute(
                    onHome = { appViewModel.enterMenu() },
                    onBack = { appViewModel.back() }
                )

                is Screen.GameOver -> GameOverRoute(
                    totalPlants = target.totalPlants,
                    onHome = { appViewModel.enterMenu() },
                    onPlanAgain = { appViewModel.navigate(Screen.CropSpacing) },
                    onRotation = { appViewModel.navigate(Screen.RotationNotes) }
                )

                is Screen.StripPlanner -> StripPlannerRoute(
                    onHome = { appViewModel.enterMenu() },
                    onBack = { appViewModel.back() }
                )

                is Screen.RotationNotes -> RotationNotesRoute(
                    onHome = { appViewModel.enterMenu() },
                    onStart = { appViewModel.navigate(Screen.BedSize) }
                )

                is Screen.Settings -> SettingsRoute(
                    onHome = { appViewModel.enterMenu() }
                )
            }
        }
    }
}
