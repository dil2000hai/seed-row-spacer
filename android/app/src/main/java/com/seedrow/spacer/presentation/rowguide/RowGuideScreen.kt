package com.seedrow.spacer.presentation.rowguide

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedrow.spacer.R
import com.seedrow.spacer.core.di.ServiceLocator
import com.seedrow.spacer.core.theme.FormOverlay
import com.seedrow.spacer.core.theme.InkPrimary
import com.seedrow.spacer.core.theme.InkSecondary
import com.seedrow.spacer.core.theme.LeafPrimary
import com.seedrow.spacer.core.theme.RadishAccent
import com.seedrow.spacer.core.theme.SageBorder
import com.seedrow.spacer.core.ui.AppBackdrop
import com.seedrow.spacer.core.ui.PaperCard
import com.seedrow.spacer.core.ui.PrimaryButton
import com.seedrow.spacer.core.ui.ScreenHeader
import com.seedrow.spacer.core.ui.SectionLabel
import com.seedrow.spacer.core.ui.spriteRes
import com.seedrow.spacer.core.ui.unitLabel
import com.seedrow.spacer.domain.model.Measure
import com.seedrow.spacer.domain.model.RowGuideEntry
import com.seedrow.spacer.domain.model.UnitSystem

@Composable
fun RowGuideRoute(
    onHome: () -> Unit,
    onBack: () -> Unit
) {
    val rowGuideViewModel: RowGuideViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by rowGuideViewModel.state.collectAsStateWithLifecycle()

    RowGuideScreen(
        state = state,
        onHome = onHome,
        onBack = onBack
    )
}

@Composable
fun RowGuideScreen(
    state: RowGuideUiState,
    onHome: () -> Unit,
    onBack: () -> Unit
) {
    val unit = unitLabel(state.units)

    AppBackdrop(overlay = FormOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_row_guide),
                subtitle = state.cropName,
                homePainter = painterResource(R.drawable.icon_home),
                homeDescription = stringResource(R.string.cd_home),
                onHome = onHome
            )

            SectionLabel(
                text = stringResource(R.string.label_in_row),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (state.entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.hint_bed_too_small),
                        style = MaterialTheme.typography.labelSmall,
                        color = RadishAccent,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(state.entries, key = { entry -> entry.index }) { entry ->
                        RowGuideCard(
                            entry = entry,
                            spriteIndex = state.spriteIndex,
                            cropName = state.cropName,
                            units = state.units,
                            unit = unit
                        )
                    }
                }
            }

            if (state.hiddenFrom > 0) {
                Text(
                    text = stringResource(
                        R.string.fmt_rows_repeat,
                        state.hiddenFrom,
                        state.hiddenTo
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }

            PrimaryButton(
                text = stringResource(R.string.cta_back_to_grid),
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun RowGuideCard(
    entry: RowGuideEntry,
    spriteIndex: Int,
    cropName: String,
    units: UnitSystem,
    unit: String
) {
    PaperCard(
        borderColor = if (entry.shiftCm > 0f) LeafPrimary else SageBorder,
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(spriteRes(spriteIndex)),
                contentDescription = cropName,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(28.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.fmt_row, entry.index),
                    style = MaterialTheme.typography.titleSmall,
                    color = InkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.fmt_strip_plants, entry.plants),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(
                        R.string.fmt_value_unit,
                        Measure.value(entry.distanceCm, units),
                        unit
                    ),
                    color = InkPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W800
                )
                if (entry.shiftCm > 0f) {
                    Text(
                        text = stringResource(
                            R.string.fmt_value_unit,
                            stringResource(
                                R.string.fmt_shift,
                                Measure.oneDecimal(entry.shiftCm)
                            ),
                            unit
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = LeafPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
