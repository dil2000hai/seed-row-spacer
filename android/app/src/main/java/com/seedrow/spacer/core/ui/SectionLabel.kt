package com.seedrow.spacer.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.seedrow.spacer.core.theme.InkSecondary

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = InkSecondary
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
    )
}
