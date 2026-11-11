package com.seedrow.spacer.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seedrow.spacer.core.config.GameConfig
import com.seedrow.spacer.core.theme.InkMuted
import com.seedrow.spacer.core.theme.InkPrimary
import com.seedrow.spacer.core.theme.InkSecondary
import com.seedrow.spacer.core.theme.LeafPrimary
import com.seedrow.spacer.core.theme.PaperSurfaceAlt
import com.seedrow.spacer.core.theme.SageBorder
import kotlinx.coroutines.delay

@Composable
fun SpacingWheel(
    label: String,
    valueText: String,
    unitText: String,
    minusText: String,
    plusText: String,
    onStep: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) InkSecondary else InkMuted,
            modifier = Modifier
                .weight(1f)
                .padding(end = 6.dp)
        )
        StepButton(
            text = minusText,
            enabled = enabled,
            onStep = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onStep(-1)
            },
            onRepeat = { onStep(-1) }
        )
        Box(
            modifier = Modifier
                .width(96.dp)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = valueText + " " + unitText,
                color = if (enabled) InkPrimary else InkMuted,
                fontSize = 20.sp,
                fontWeight = FontWeight.W800,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
        StepButton(
            text = plusText,
            enabled = enabled,
            onStep = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onStep(1)
            },
            onRepeat = { onStep(1) }
        )
    }
}

@Composable
private fun StepButton(
    text: String,
    enabled: Boolean,
    onStep: () -> Unit,
    onRepeat: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val repeatAction by rememberUpdatedState(onRepeat)

    LaunchedEffect(pressed, enabled) {
        if (pressed && enabled) {
            delay(GameConfig.WHEEL_HOLD_DELAY_MS)
            while (true) {
                repeatAction()
                delay(GameConfig.WHEEL_REPEAT_MS)
            }
        }
    }

    TextButton(
        onClick = onStep,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(PaperSurfaceAlt, RoundedCornerShape(16.dp))
                .border(BorderStroke(1.5.dp, SageBorder), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (enabled) LeafPrimary else InkMuted,
                fontSize = 22.sp,
                fontWeight = FontWeight.W800
            )
        }
    }
}
