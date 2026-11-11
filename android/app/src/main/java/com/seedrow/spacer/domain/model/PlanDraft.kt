package com.seedrow.spacer.domain.model

data class PlanDraft(
    val bed: Bed,
    val crop: Crop,
    val inRowCm: Int,
    val betweenRowCm: Int,
    val gridKind: GridKind,
    val sownEpochDay: Long
)
