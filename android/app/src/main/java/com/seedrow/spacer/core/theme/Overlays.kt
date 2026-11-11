package com.seedrow.spacer.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val SplashOverlay = Brush.verticalGradient(
    listOf(Color(0xE61B2B22), Color(0xF21B2B22), Color(0xFA12201A))
)

val MenuOverlay = Brush.verticalGradient(
    listOf(Color(0xF2F6F3E7), Color(0xE6F2EAD8), Color(0xF7E8DDC4))
)

val FormOverlay = Brush.verticalGradient(
    listOf(Color(0xF2F6F3E7), Color(0xEFE8DDC4))
)

val CropOverlay = Brush.verticalGradient(
    listOf(Color(0xF0F6F3E7), Color(0xEBF2EAD8), Color(0xF2E8DDC4))
)

val GameOverlay = Brush.verticalGradient(
    listOf(Color(0xE6F6F3E7), Color(0xF2E8DDC4), Color(0xF7D8C9A8))
)

val ResultOverlay = Brush.verticalGradient(
    listOf(Color(0xF2D8E2C6), Color(0xF2F6F3E7), Color(0xF7E8DDC4))
)

val SoilBrush = Brush.verticalGradient(listOf(SoilTop, SoilBottom))

val PrimaryBrush = Brush.linearGradient(listOf(LeafPrimary, LeafLight))
