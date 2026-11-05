package com.seedrow.spacer.presentation.bedsize

import androidx.compose.foundation.layout.Column
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
import com.seedrow.spacer.core.theme.FormOverlay
import com.seedrow.spacer.core.theme.InkSecondary
import com.seedrow.spacer.core.theme.RadishAccent
import com.seedrow.spacer.core.theme.SageBorder
import com.seedrow.spacer.core.ui.AppBackdrop
import com.seedrow.spacer.core.ui.BedCanvas
import com.seedrow.spacer.core.ui.PaperCard
import com.seedrow.spacer.core.ui.PrimaryButton
import com.seedrow.spacer.core.ui.ScreenHeader
import com.seedrow.spacer.core.ui.SectionLabel
import com.seedrow.spacer.core.ui.SpacingWheel
import com.seedrow.spacer.core.ui.unitLabel
import com.seedrow.spacer.domain.model.Measure

@Composable
fun BedSizeRoute(
    onHome: () -> Unit,
    onNext: () -> Unit
) {
    val bedSizeViewModel: BedSizeViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by bedSizeViewModel.state.collectAsStateWithLifecycle()
    BedSizeScreen(
        state = state,
        onHome = onHome,
        onNext = onNext,
        onStepLength = bedSizeViewModel::stepLength,
        onStepWidth = bedSizeViewModel::stepWidth,
        onStepPath = bedSizeViewModel::stepPath
    )
}

@Composable
fun BedSizeScreen(
    state: BedSizeUiState,
    onHome: () -> Unit,
    onNext: () -> Unit,
    onStepLength: (Int) -> Unit,
    onStepWidth: (Int) -> Unit,
    onStepPath: (Int) -> Unit
) {
    val unit = unitLabel(state.units)
    AppBackdrop(overlay = FormOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_bed_size),
                subtitle = stringResource(R.string.fmt_step, 1, 3),
                homePainter = painterResource(R.drawable.icon_home),
                homeDescription = stringResource(R.string.cd_home),
                onHome = onHome
            )

            Text(
                text = state.bedName,
                style = MaterialTheme.typography.titleMedium,
                color = InkSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            PaperCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    SpacingWheel(
                        label = stringResource(R.string.label_length),
                        valueText = Measure.value(state.lengthCm, state.units),
                        unitText = unit,
                        minusText = stringResource(R.string.step_minus),
                        plusText = stringResource(R.string.step_plus),
                        onStep = onStepLength
                    )
                    HorizontalDivider(thickness = 1.dp, color = SageBorder)
                    SpacingWheel(
                        label = stringResource(R.string.label_width),
                        valueText = Measure.value(state.widthCm, state.units),
                        unitText = unit,
                        minusText = stringResource(R.string.step_minus),
                        plusText = stringResource(R.string.step_plus),
                        onStep = onStepWidth
                    )
                    HorizontalDivider(thickness = 1.dp, color = SageBorder)
                    SpacingWheel(
                        label = stringResource(R.string.label_path),
                        valueText = Measure.value(state.pathCm, state.units),
                        unitText = unit,
                        minusText = stringResource(R.string.step_minus),
                        plusText = stringResource(R.string.step_plus),
                        onStep = onStepPath
                    )
                }
            }

            SectionLabel(
                text = stringResource(R.string.label_bed_preview),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            )

            BedCanvas(
                lengthCm = state.lengthCm,
                widthCm = state.widthCm,
                inRowCm = state.inRowCm,
                rowPitchCm = 1f,
                rows = 0,
                columns = 0,
                spriteIndex = 1,
                revealProgress = 0f,
                shiftFactor = 0f,
                oddRowShorter = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Text(
                text = stringResource(
                    R.string.fmt_size,
                    Measure.value(state.lengthCm, state.units),
                    Measure.value(state.widthCm, state.units),
                    unit
                ) + "  " + stringResource(R.string.fmt_area, Measure.area(state.areaM2)),
                style = MaterialTheme.typography.labelSmall,
                color = InkSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (!state.valid) {
                Text(
                    text = stringResource(R.string.hint_min_size),
                    style = MaterialTheme.typography.labelSmall,
                    color = RadishAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            PrimaryButton(
                text = stringResource(R.string.cta_next_crop),
                onClick = onNext,
                enabled = state.valid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}
