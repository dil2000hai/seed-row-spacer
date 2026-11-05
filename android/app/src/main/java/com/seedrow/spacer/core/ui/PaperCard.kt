package com.seedrow.spacer.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seedrow.spacer.core.theme.PaperGlass
import com.seedrow.spacer.core.theme.SageBorder

@Composable
fun PaperCard(
    modifier: Modifier = Modifier,
    containerColor: Color = PaperGlass,
    borderColor: Color = SageBorder,
    elevation: Dp = 6.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        content = content
    )
}
