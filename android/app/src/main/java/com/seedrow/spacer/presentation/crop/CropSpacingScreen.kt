package com.seedrow.spacer.presentation.crop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedrow.spacer.R
import com.seedrow.spacer.core.di.ServiceLocator
import com.seedrow.spacer.core.theme.CropOverlay
import com.seedrow.spacer.core.theme.InkPrimary
import com.seedrow.spacer.core.theme.InkSecondary
import com.seedrow.spacer.core.theme.RadishAccent
import com.seedrow.spacer.core.theme.SageBorder
import com.seedrow.spacer.core.ui.AppBackdrop
import com.seedrow.spacer.core.ui.PaperCard
import com.seedrow.spacer.core.ui.PrimaryButton
import com.seedrow.spacer.core.ui.ScreenHeader
import com.seedrow.spacer.core.ui.SecondaryButton
import com.seedrow.spacer.core.ui.SectionLabel
import com.seedrow.spacer.core.ui.SeedPacketChip
import com.seedrow.spacer.core.ui.SpacingWheel
import com.seedrow.spacer.core.ui.unitLabel
import com.seedrow.spacer.domain.model.Measure

@Composable
fun CropSpacingRoute(
    onHome: () -> Unit,
    onBack: () -> Unit,
    onShowGrid: () -> Unit
) {
    val cropViewModel: CropSpacingViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by cropViewModel.state.collectAsStateWithLifecycle()
    CropSpacingScreen(
        state = state,
        onHome = onHome,
        onBack = onBack,
        onShowGrid = onShowGrid,
        onPickCrop = cropViewModel::pickCrop,
        onStepInRow = cropViewModel::stepInRow,
        onStepBetweenRows = cropViewModel::stepBetweenRows,
        onStepDay = cropViewModel::stepSowingDay
    )
}

@Composable
fun CropSpacingScreen(
    state: CropSpacingUiState,
    onHome: () -> Unit,
    onBack: () -> Unit,
    onShowGrid: () -> Unit,
    onPickCrop: (String) -> Unit,
    onStepInRow: (Int) -> Unit,
    onStepBetweenRows: (Int) -> Unit,
    onStepDay: (Int) -> Unit
) {
    val unit = unitLabel(state.units)
    AppBackdrop(overlay = CropOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_crop),
                subtitle = stringResource(R.string.fmt_step, 2, 3),
                homePainter = painterResource(R.drawable.icon_home),
                homeDescription = stringResource(R.string.cd_home),
                onHome = onHome
            )

            SectionLabel(
                text = stringResource(R.string.label_crop),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.crops.forEach { crop ->
                    SeedPacketChip(
                        name = crop.name,
                        spriteIndex = crop.spriteIndex,
                        selected = crop.id == state.selectedCropId,
                        stateText = crop.name,
                        onClick = { onPickCrop(crop.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            SectionLabel(
                text = stringResource(R.string.label_spacing),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            PaperCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    SpacingWheel(
                        label = stringResource(R.string.label_in_row),
                        valueText = Measure.value(state.inRowCm, state.units),
                        unitText = unit,
                        minusText = stringResource(R.string.step_minus),
                        plusText = stringResource(R.string.step_plus),
                        onStep = onStepInRow
                    )
                    HorizontalDivider(thickness = 1.dp, color = SageBorder)
                    SpacingWheel(
                        label = stringResource(R.string.label_between_rows),
                        valueText = Measure.value(state.betweenRowCm, state.units),
                        unitText = unit,
                        minusText = stringResource(R.string.step_minus),
                        plusText = stringResource(R.string.step_plus),
                        onStep = onStepBetweenRows
                    )
                    HorizontalDivider(thickness = 1.dp, color = SageBorder)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.label_sowing),
                                style = MaterialTheme.typography.labelSmall,
                                color = InkSecondary
                            )
                            Text(
                                text = state.sownText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = InkPrimary
                            )
                        }
                        SecondaryButton(
                            text = stringResource(R.string.step_minus_day),
                            onClick = { onStepDay(-1) },
                            height = 44.dp
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 3.dp))
                        SecondaryButton(
                            text = stringResource(R.string.step_plus_day),
                            onClick = { onStepDay(1) },
                            height = 44.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state.valid) {
                        stringResource(R.string.hint_pick_crop)
                    } else {
                        stringResource(R.string.hint_bed_too_small)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.valid) InkSecondary else RadishAccent,
                    textAlign = TextAlign.Center
                )
            }

            SecondaryButton(
                text = stringResource(R.string.cta_back),
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            )

            PrimaryButton(
                text = stringResource(R.string.cta_show_grid),
                onClick = onShowGrid,
                enabled = state.valid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 16.dp)
            )
        }
    }
}
