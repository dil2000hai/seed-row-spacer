package com.seedrow.spacer.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object BedSize : Screen
    data object CropSpacing : Screen
    data object Game : Screen
    data object RowGuide : Screen
    data class GameOver(val totalPlants: Int) : Screen
    data object StripPlanner : Screen
    data object RotationNotes : Screen
    data object Settings : Screen
}
