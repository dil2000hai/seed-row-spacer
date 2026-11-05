package com.seedrow.spacer.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.seedrow.spacer.R
import com.seedrow.spacer.domain.model.UnitSystem

@Composable
fun unitLabel(unit: UnitSystem): String = when (unit) {
    UnitSystem.CM -> stringResource(R.string.unit_cm)
    UnitSystem.INCH -> stringResource(R.string.unit_in)
}
