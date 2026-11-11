package com.seedrow.spacer.presentation.rotation

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
import com.seedrow.spacer.core.theme.CropOverlay
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

@Composable
fun RotationNotesRoute(
    onHome: () -> Unit,
    onStart: () -> Unit
) {
    val rotationViewModel: RotationNotesViewModel =
        viewModel(factory = ServiceLocator.viewModelFactory)
    val state by rotationViewModel.state.collectAsStateWithLifecycle()

    RotationNotesScreen(
        state = state,
        onHome = onHome,
        onStart = onStart
    )
}

@Composable
fun RotationNotesScreen(
    state: RotationNotesUiState,
    onHome: () -> Unit,
    onStart: () -> Unit
) {
    AppBackdrop(overlay = CropOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_rotation),
                homePainter = painterResource(R.drawable.icon_home),
                homeDescription = stringResource(R.string.cd_home),
                onHome = onHome
            )

            SectionLabel(
                text = stringResource(R.string.label_bed),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (state.notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.empty_rotation),
                        style = MaterialTheme.typography.labelSmall,
                        color = InkSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(state.notes, key = { note -> note.id }) { note ->
                        RotationCard(note = note)
                    }
                }
            }

            if (state.warningCount > 0) {
                Text(
                    text = stringResource(R.string.warning_rotation),
                    style = MaterialTheme.typography.labelSmall,
                    color = RadishAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }

            PrimaryButton(
                text = stringResource(R.string.cta_start),
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun RotationCard(note: RotationNote) {
    val stateText = if (note.warning) {
        stringResource(R.string.warning_rotation)
    } else {
        stringResource(R.string.title_rotation)
    }
    PaperCard(
        borderColor = if (note.warning) RadishAccent else SageBorder,
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .semantics { stateDescription = stateText }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(spriteRes(note.spriteIndex)),
                contentDescription = note.cropName,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(30.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = note.bedName,
                    style = MaterialTheme.typography.titleSmall,
                    color = InkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = note.cropName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (note.warning) RadishAccent else InkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.fmt_count, note.year),
                    color = LeafPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W800
                )
                Text(
                    text = note.cropFamily,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}
