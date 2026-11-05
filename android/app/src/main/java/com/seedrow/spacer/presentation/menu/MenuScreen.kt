package com.seedrow.spacer.presentation.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.seedrow.spacer.core.theme.MenuOverlay
import com.seedrow.spacer.core.theme.PaperBg
import com.seedrow.spacer.core.theme.PaperGlassSoft
import com.seedrow.spacer.core.theme.PrimaryBrush
import com.seedrow.spacer.core.theme.RadishAccent
import com.seedrow.spacer.core.theme.SageBorder
import com.seedrow.spacer.core.ui.AppBackdrop
import com.seedrow.spacer.core.ui.PaperCard
import com.seedrow.spacer.core.ui.SectionLabel
import com.seedrow.spacer.core.ui.StatCard
import com.seedrow.spacer.core.ui.spriteRes
import com.seedrow.spacer.core.ui.unitLabel
import com.seedrow.spacer.domain.model.Bed
import com.seedrow.spacer.domain.model.Measure

@Composable
fun MenuRoute(
    onStart: () -> Unit,
    onStrips: () -> Unit,
    onRotation: () -> Unit,
    onRowGuide: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    MenuScreen(
        state = state,
        onStart = onStart,
        onPickBed = { bedId ->
            menuViewModel.pickBed(bedId)
            onStart()
        },
        onStrips = onStrips,
        onRotation = onRotation,
        onRowGuide = onRowGuide,
        onSettings = onSettings
    )
}

@Composable
fun MenuScreen(
    state: MenuUiState,
    onStart: () -> Unit,
    onPickBed: (String) -> Unit,
    onStrips: () -> Unit,
    onRotation: () -> Unit,
    onRowGuide: () -> Unit,
    onSettings: () -> Unit
) {
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        entered = true
    }
    val rise by animateFloatAsState(
        targetValue = if (entered) 0f else 10f,
        animationSpec = tween(360)
    )
    val fade by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(360)
    )
    val showStats = state.plantsInSelectedBed > 0 && state.bedCount > 0

    AppBackdrop(overlay = MenuOverlay) {
        Image(
            painter = painterResource(spriteRes(5)),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.05f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.app_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = InkPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSettings, modifier = Modifier.size(48.dp)) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_settings),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            SectionLabel(
                text = stringResource(
                    R.string.fmt_season,
                    state.season,
                    state.bedCount,
                    state.cropCount
                )
            )

            Text(
                text = stringResource(R.string.hint_menu),
                style = MaterialTheme.typography.labelSmall,
                color = InkSecondary,
                modifier = Modifier.padding(top = 10.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp)
                    .graphicsLayer {
                        translationY = rise
                        alpha = fade
                    },
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StartTile(
                    text = stringResource(R.string.cta_start),
                    onClick = onStart,
                    modifier = Modifier
                        .weight(if (showStats) 2f else 1f)
                        .fillMaxHeight()
                )
                if (showStats) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            value = state.plantsInSelectedBed.toString(),
                            label = stringResource(R.string.label_plants),
                            valueColor = RadishAccent,
                            live = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        StatCard(
                            value = state.bedCount.toString(),
                            label = stringResource(R.string.label_beds),
                            valueColor = LeafPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(96.dp)
                    .graphicsLayer {
                        translationY = rise * 1.6f
                        alpha = fade
                    },
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoTile(
                    text = stringResource(R.string.tile_strip),
                    onClick = onStrips,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                BentoTile(
                    text = stringResource(R.string.tile_rotation),
                    onClick = onRotation,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                BentoTile(
                    text = stringResource(R.string.tile_row_guide),
                    onClick = onRowGuide,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            SectionLabel(
                text = stringResource(R.string.label_recent_beds),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.recentBeds, key = { bed -> bed.id }) { bed ->
                    BedRow(
                        bed = bed,
                        selected = bed.id == state.selectedBedId,
                        sizeText = stringResource(
                            R.string.fmt_size,
                            Measure.value(bed.lengthCm, state.units),
                            Measure.value(bed.widthCm, state.units),
                            unitLabel(state.units)
                        ),
                        onClick = { onPickBed(bed.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StartTile(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = 420f)
    )
    val shape = RoundedCornerShape(18.dp)
    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = shape,
        elevation = null,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = PaperBg
        ),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(10.dp, shape, ambientColor = LeafPrimary, spotColor = LeafPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(PrimaryBrush),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = PaperBg,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(56.dp)
                    .alpha(0.9f)
            )
        }
    }
}

@Composable
private fun BentoTile(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .shadow(3.dp, shape)
            .clip(shape)
            .background(PaperGlassSoft)
            .border(BorderStroke(1.5.dp, SageBorder), shape)
            .clickable(onClick = onClick)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = LeafPrimary,
            fontSize = 12.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.W700,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun BedRow(
    bed: Bed,
    selected: Boolean,
    sizeText: String,
    onClick: () -> Unit
) {
    PaperCard(
        borderColor = if (selected) LeafPrimary else SageBorder,
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bed.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = InkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = sizeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Image(
                    painter = painterResource(spriteRes(1)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(22.dp)
                )
                Image(
                    painter = painterResource(spriteRes(2)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(22.dp)
                )
                Image(
                    painter = painterResource(spriteRes(3)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.size(2.dp))
        }
    }
}
