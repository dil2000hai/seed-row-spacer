package com.seedrow.spacer.presentation.gameover

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedrow.spacer.R
import com.seedrow.spacer.core.di.ServiceLocator
import com.seedrow.spacer.core.theme.InkPrimary
import com.seedrow.spacer.core.theme.InkSecondary
import com.seedrow.spacer.core.theme.LeafPrimary
import com.seedrow.spacer.core.theme.RadishAccent
import com.seedrow.spacer.core.theme.ResultOverlay
import com.seedrow.spacer.core.theme.TerracottaAlt
import com.seedrow.spacer.core.ui.AppBackdrop
import com.seedrow.spacer.core.ui.PaperCard
import com.seedrow.spacer.core.ui.PrimaryButton
import com.seedrow.spacer.core.ui.ScreenHeader
import com.seedrow.spacer.core.ui.SecondaryButton
import com.seedrow.spacer.core.ui.StatCard
import com.seedrow.spacer.core.ui.spriteRes
import com.seedrow.spacer.core.ui.unitLabel
import com.seedrow.spacer.domain.model.GridKind
import com.seedrow.spacer.domain.model.Measure

@Composable
fun GameOverRoute(
    totalPlants: Int,
    onHome: () -> Unit,
    onPlanAgain: () -> Unit,
    onRotation: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(totalPlants) {
        gameOverViewModel.onResult(totalPlants)
    }

    GameOverScreen(
        state = state,
        onHome = onHome,
        onPlanAgain = onPlanAgain,
        onSaveRotation = {
            gameOverViewModel.saveToRotation()
            onRotation()
        }
    )
}

@Composable
fun GameOverScreen(
    state: GameOverUiState,
    onHome: () -> Unit,
    onPlanAgain: () -> Unit,
    onSaveRotation: () -> Unit
) {
    val unit = unitLabel(state.units)
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(state.cropName) {
        entered = true
    }
    val rise by animateFloatAsState(
        targetValue = if (entered) 0f else 14f,
        animationSpec = tween(380, easing = FastOutSlowInEasing)
    )
    val fade by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(380, easing = FastOutSlowInEasing)
    )
    val savedState = if (state.savedToRotation) {
        stringResource(R.string.label_strips)
    } else {
        stringResource(R.string.title_rotation)
    }

    AppBackdrop(overlay = ResultOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_result),
                subtitle = state.bedName,
                homePainter = painterResource(R.drawable.icon_home),
                homeDescription = stringResource(R.string.cd_home),
                onHome = onHome
            )

            PaperCard(
                borderColor = LeafPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .graphicsLayer {
                        translationY = rise
                        alpha = fade
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.cropName,
                            style = MaterialTheme.typography.titleMedium,
                            color = InkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = state.totalPlants.toString(),
                            color = LeafPrimary,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.W800,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .semantics { liveRegion = LiveRegionMode.Polite }
                        )
                        Text(
                            text = if (state.totalPlants > 0) {
                                stringResource(R.string.label_plants_fit)
                            } else {
                                stringResource(R.string.label_nothing_fits)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (state.totalPlants > 0) InkSecondary else RadishAccent
                        )
                    }
                    Image(
                        painter = painterResource(spriteRes(state.spriteIndex)),
                        contentDescription = state.cropName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(84.dp)
                    )
                }
            }

            if (state.seedsNeeded > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(86.dp)
                        .graphicsLayer {
                            translationY = rise * 1.5f
                            alpha = fade
                        },
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        value = state.seedsNeeded.toString(),
                        label = stringResource(R.string.label_seeds_needed),
                        valueColor = LeafPrimary,
                        live = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    )
                    StatCard(
                        value = state.seedsWithAllowance.toString(),
                        label = stringResource(R.string.label_with_allowance),
                        valueColor = TerracottaAlt,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    )
                    if (state.germinationPercent > 0) {
                        StatCard(
                            value = stringResource(R.string.fmt_percent, state.germinationPercent),
                            label = stringResource(R.string.label_germination),
                            valueColor = RadishAccent,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                    }
                }
            }

            PaperCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .graphicsLayer {
                        translationY = rise * 2f
                        alpha = fade
                    }
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    DetailRow(
                        label = stringResource(R.string.label_grid),
                        value = if (state.gridKind == GridKind.OFFSET) {
                            stringResource(R.string.toggle_offset)
                        } else {
                            stringResource(R.string.toggle_square)
                        }
                    )
                    DetailRow(
                        label = stringResource(R.string.label_spacing),
                        value = stringResource(
                            R.string.fmt_spacing,
                            Measure.value(state.inRowCm, state.units),
                            Measure.value(state.betweenRowCm, state.units),
                            unit
                        )
                    )
                    DetailRow(
                        label = stringResource(R.string.label_bed),
                        value = stringResource(R.string.fmt_area, Measure.area(state.areaM2))
                    )
                    DetailRow(
                        label = stringResource(R.string.label_sown),
                        value = state.sownText
                    )
                    DetailRow(
                        label = stringResource(R.string.label_harvest),
                        value = stringResource(
                            R.string.fmt_range,
                            state.harvestFromText,
                            state.harvestToText
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.hint_menu),
                style = MaterialTheme.typography.labelSmall,
                color = InkSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.cta_again),
                onClick = onPlanAgain,
                modifier = Modifier.fillMaxWidth()
            )

            SecondaryButton(
                text = stringResource(R.string.cta_save_rotation),
                onClick = onSaveRotation,
                enabled = !state.savedToRotation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .semantics { stateDescription = savedState }
            )

            SecondaryButton(
                text = stringResource(R.string.cta_menu),
                onClick = onHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = InkSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = InkPrimary,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
