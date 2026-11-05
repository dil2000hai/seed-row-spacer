package com.seedrow.spacer.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.seedrow.spacer.core.theme.GameOverlay
import com.seedrow.spacer.core.theme.InkSecondary
import com.seedrow.spacer.core.theme.LeafPrimary
import com.seedrow.spacer.core.theme.RadishAccent
import com.seedrow.spacer.core.ui.AppBackdrop
import com.seedrow.spacer.core.ui.BedCanvas
import com.seedrow.spacer.core.ui.PrimaryButton
import com.seedrow.spacer.core.ui.ScreenHeader
import com.seedrow.spacer.core.ui.SecondaryButton
import com.seedrow.spacer.core.ui.SegmentToggle
import com.seedrow.spacer.core.ui.StatCard
import com.seedrow.spacer.core.ui.unitLabel
import com.seedrow.spacer.domain.model.GridKind
import com.seedrow.spacer.domain.model.Measure

@Composable
fun GameRoute(
    onHome: () -> Unit,
    onRowGuide: () -> Unit,
    onResult: (Int) -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        gameViewModel.onEnter()
        onDispose { gameViewModel.pause() }
    }

    GameScreen(
        state = state,
        onHome = onHome,
        onRowGuide = onRowGuide,
        onResult = { onResult(state.totalPlants) },
        onTogglePause = gameViewModel::togglePause,
        onSelectGrid = gameViewModel::selectGrid
    )
}

@Composable
fun GameScreen(
    state: GameUiState,
    onHome: () -> Unit,
    onRowGuide: () -> Unit,
    onResult: () -> Unit,
    onTogglePause: () -> Unit,
    onSelectGrid: (GridKind) -> Unit
) {
    val unit = unitLabel(state.units)
    val revealProgress by animateFloatAsState(
        targetValue = state.revealedRows.toFloat(),
        animationSpec = tween(420, easing = FastOutSlowInEasing)
    )
    val shiftFactor by animateFloatAsState(
        targetValue = if (state.gridKind == GridKind.OFFSET) 1f else 0f,
        animationSpec = tween(380, easing = FastOutSlowInEasing)
    )
    val pauseState = if (state.paused) {
        stringResource(R.string.state_paused)
    } else {
        stringResource(R.string.state_playing)
    }

    AppBackdrop(overlay = GameOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            ScreenHeader(
                title = state.cropName,
                subtitle = stringResource(
                    R.string.fmt_spacing,
                    Measure.value(state.inRowCm, state.units),
                    Measure.value(state.betweenRowCm, state.units),
                    unit
                ),
                homePainter = painterResource(R.drawable.icon_home),
                homeDescription = stringResource(R.string.cd_home),
                onHome = onHome,
                actionPainter = painterResource(R.drawable.icon_pause),
                actionDescription = if (state.paused) {
                    stringResource(R.string.cd_resume)
                } else {
                    stringResource(R.string.cd_pause)
                },
                actionState = pauseState,
                onAction = onTogglePause
            )

            SegmentToggle(
                options = listOf(
                    stringResource(R.string.toggle_square),
                    stringResource(R.string.toggle_offset)
                ),
                selectedIndex = if (state.gridKind == GridKind.OFFSET) 1 else 0,
                stateText = if (state.gridKind == GridKind.OFFSET) {
                    stringResource(R.string.toggle_offset)
                } else {
                    stringResource(R.string.toggle_square)
                },
                onSelect = { index ->
                    onSelectGrid(if (index == 1) GridKind.OFFSET else GridKind.SQUARE)
                },
                modifier = Modifier.fillMaxWidth()
            )

            BedCanvas(
                lengthCm = state.lengthCm,
                widthCm = state.widthCm,
                inRowCm = state.inRowCm,
                rowPitchCm = state.rowPitchCm,
                rows = state.rows,
                columns = state.columns,
                spriteIndex = state.spriteIndex,
                revealProgress = revealProgress,
                shiftFactor = shiftFactor,
                oddRowShorter = state.gridKind == GridKind.OFFSET,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        R.string.fmt_value_unit,
                        Measure.value(state.inRowCm, state.units),
                        unit
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary
                )
                Text(
                    text = stringResource(
                        R.string.fmt_value_unit,
                        Measure.value(state.betweenRowCm, state.units),
                        unit
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary
                )
            }

            if (state.fits) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .height(78.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        value = state.squarePlants.toString(),
                        label = stringResource(R.string.label_square),
                        valueColor = InkSecondary,
                        live = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    )
                    StatCard(
                        value = state.offsetPlants.toString(),
                        label = stringResource(R.string.label_offset),
                        valueColor = RadishAccent,
                        live = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    )
                    if (state.gainPercent != 0) {
                        StatCard(
                            value = stringResource(R.string.fmt_gain, state.gainPercent),
                            label = stringResource(R.string.label_gain),
                            valueColor = LeafPrimary,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.hint_bed_too_small),
                    style = MaterialTheme.typography.labelSmall,
                    color = RadishAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp)
                )
            }

            SecondaryButton(
                text = stringResource(R.string.cta_row_guide),
                onClick = onRowGuide,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.cta_seed_count),
                onClick = onResult,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 16.dp)
            )
        }
    }
}
