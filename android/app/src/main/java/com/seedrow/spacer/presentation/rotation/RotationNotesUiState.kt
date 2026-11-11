package com.seedrow.spacer.presentation.rotation

data class RotationNote(
    val id: String,
    val bedName: String,
    val year: Int,
    val cropName: String,
    val cropFamily: String,
    val spriteIndex: Int,
    val warning: Boolean
)

data class RotationNotesUiState(
    val notes: List<RotationNote> = emptyList(),
    val warningCount: Int = 0
)
