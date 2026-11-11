package com.seedrow.spacer.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedrow.spacer.R
import com.seedrow.spacer.core.di.ServiceLocator
import com.seedrow.spacer.core.theme.FormOverlay
import com.seedrow.spacer.core.theme.InkPrimary
import com.seedrow.spacer.core.theme.InkSecondary
import com.seedrow.spacer.core.ui.AppBackdrop
import com.seedrow.spacer.core.ui.PaperCard
import com.seedrow.spacer.core.ui.PrimaryButton
import com.seedrow.spacer.core.ui.ScreenHeader
import com.seedrow.spacer.core.ui.SecondaryButton
import com.seedrow.spacer.core.ui.SectionLabel
import com.seedrow.spacer.core.ui.SeedPacketChip
import com.seedrow.spacer.core.ui.SegmentToggle
import com.seedrow.spacer.domain.model.UnitSystem

@Composable
fun SettingsRoute(
    onHome: () -> Unit
) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onHome = onHome,
        onSelectUnits = settingsViewModel::selectUnits,
        onPickCrop = settingsViewModel::pickCrop,
        onStepFrost = settingsViewModel::stepFrost
    )
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onHome: () -> Unit,
    onSelectUnits: (UnitSystem) -> Unit,
    onPickCrop: (String) -> Unit,
    onStepFrost: (Int) -> Unit
) {
    val cmLabel = stringResource(R.string.toggle_cm)
    val inchLabel = stringResource(R.string.toggle_inch)
    val selectedLabel = stringResource(R.string.label_default_crop)

    AppBackdrop(overlay = FormOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.title_settings),
                homePainter = painterResource(R.drawable.icon_home),
                homeDescription = stringResource(R.string.cd_home),
                onHome = onHome
            )

            SectionLabel(
                text = stringResource(R.string.label_units),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SegmentToggle(
                options = listOf(cmLabel, inchLabel),
                selectedIndex = if (state.units == UnitSystem.INCH) 1 else 0,
                stateText = if (state.units == UnitSystem.INCH) inchLabel else cmLabel,
                onSelect = { index ->
                    onSelectUnits(if (index == 1) UnitSystem.INCH else UnitSystem.CM)
                },
                modifier = Modifier.fillMaxWidth()
            )

            SectionLabel(
                text = selectedLabel,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(state.crops, key = { crop -> crop.id }) { crop ->
                    SeedPacketChip(
                        name = crop.name,
                        spriteIndex = crop.spriteIndex,
                        selected = crop.id == state.defaultCropId,
                        stateText = selectedLabel,
                        onClick = { onPickCrop(crop.id) },
                        modifier = Modifier.width(96.dp)
                    )
                }
            }

            SectionLabel(
                text = stringResource(R.string.label_last_frost),
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )

            PaperCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    Text(
                        text = state.frostText,
                        color = InkPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W800,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SecondaryButton(
                            text = stringResource(R.string.step_minus_day),
                            onClick = { onStepFrost(-1) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                        SecondaryButton(
                            text = stringResource(R.string.step_plus_day),
                            onClick = { onStepFrost(1) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.hint_pick_crop),
                style = MaterialTheme.typography.labelSmall,
                color = InkSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.cta_menu),
                onClick = onHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}
