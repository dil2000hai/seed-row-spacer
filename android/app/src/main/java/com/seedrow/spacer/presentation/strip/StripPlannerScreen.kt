package com.seedrow.spacer.presentation.strip

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedrow.spacer.R
import com.seedrow.spacer.core.di.ServiceLocator
import com.seedrow.spacer.core.theme.FormOverlay
import com.seedrow.spacer.core.theme.InkSecondary
import com.seedrow.spacer.core.theme.LeafPrimary
import com.seedrow.spacer.core.theme.SproutGreen
import com.seedrow.spacer.core.ui.AppBackdrop
import com.seedrow.spacer.core.ui.PaperCard
import com.seedrow.spacer.core.ui.PrimaryButton
import com.seedrow.spacer.core.ui.ScreenHeader
import com.seedrow.spacer.core.ui.SecondaryButton
import com.seedrow.spacer.core.ui.SectionLabel
import com.seedrow.spacer.core.ui.SpacingWheel
import com.seedrow.spacer.core.ui.StatCard
import com.seedrow.spacer.core.ui.spriteRes
import com.seedrow.spacer.core.ui.unitLabel
import com.seedrow.spacer.domain.model.Measure
import com.seedrow.spacer.domain.model.Strip
import com.seedrow.spacer.domain.model.UnitSystem

@Composable
fun StripPlannerRoute(
    onHome: () -> Unit,
    onBack: () -> Unit
) {
    val stripViewModel: StripPlannerViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by stripViewModel.state.collectAsStateWithLifecycle()

    StripPlannerScreen(
        state = state,
        onHome = onHome,
        onBack = onBack,
        onStep = stripViewModel::stepWidth,
        onApply = {
            stripViewModel.applyBestStrip()
            onBack()
        }
    )
}

@Composable
fun StripPlannerScreen(
    state: StripPlannerUiState,
    onHome: () -> Unit,
    onBack: () -> Unit,
    onStep: (Int, Int) -> Unit,
    onApply: () -> Unit
) {
    val unit = unitLabel(state.units)
    val freeFraction by animateFloatAsState(
        targetValue = if (state.lengthCm > 0) {
            state.freeCm.toFloat() / state.lengthCm.toFloat()
        } else {
            0f
        },
        animationSpec = tween(320)
    )

    AppBackdrop(overlay = FormOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_strip),
                subtitle = state.bedName,
                homePainter = painterResource(R.drawable.icon_home),
                homeDescription = stringResource(R.string.cd_home),
                onHome = onHome
            )

            SectionLabel(
                text = stringResource(R.string.label_strips),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.strips.forEachIndexed { index, strip ->
                    StripRow(
                        strip = strip,
                        units = state.units,
                        unit = unit,
                        minusText = stringResource(R.string.step_minus),
                        plusText = stringResource(R.string.step_plus),
                        onStep = { direction -> onStep(index, direction) }
                    )
                }
            }

            if (state.totalPlants > 0 && state.freeCm > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(86.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        value = state.totalPlants.toString(),
                        label = stringResource(R.string.label_plants),
                        valueColor = LeafPrimary,
                        live = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    )
                    StatCard(
                        value = Measure.value(state.freeCm, state.units),
                        label = stringResource(R.string.label_free),
                        valueColor = SproutGreen,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    )
                }
            }

            Text(
                text = stringResource(
                    R.string.fmt_free,
                    Measure.value(state.freeCm, state.units),
                    unit
                ),
                style = MaterialTheme.typography.labelSmall,
                color = if (freeFraction > 0.05f) SproutGreen else InkSecondary,
                modifier = Modifier.padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.hint_strip),
                style = MaterialTheme.typography.labelSmall,
                color = InkSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.cta_apply_strips),
                onClick = onApply,
                enabled = state.totalPlants > 0,
                modifier = Modifier.fillMaxWidth()
            )

            SecondaryButton(
                text = stringResource(R.string.cta_back),
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun StripRow(
    strip: Strip,
    units: UnitSystem,
    unit: String,
    minusText: String,
    plusText: String,
    onStep: (Int) -> Unit
) {
    PaperCard(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(spriteRes(strip.spriteIndex)),
                    contentDescription = strip.cropName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = stringResource(R.string.fmt_strip_plants, strip.plants),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
            }
            SpacingWheel(
                label = strip.cropName,
                valueText = Measure.value(strip.widthCm, units),
                unitText = unit,
                minusText = minusText,
                plusText = plusText,
                onStep = onStep
            )
        }
    }
}
