package com.seedrow.spacer.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seedrow.spacer.core.theme.InkPrimary
import com.seedrow.spacer.core.theme.LeafPrimary
import com.seedrow.spacer.core.theme.PaperGlass
import com.seedrow.spacer.core.theme.SageBorder

@Composable
fun SeedPacketChip(
    name: String,
    spriteIndex: Int,
    selected: Boolean,
    stateText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 104.dp
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 420f)
    )
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(if (selected) Color(0xFFE8F0E4) else PaperGlass)
            .border(
                BorderStroke(if (selected) 2.dp else 1.5.dp, if (selected) LeafPrimary else SageBorder),
                shape
            )
            .clickable(onClick = onClick)
            .semantics { stateDescription = stateText },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(SageBorder)
        )
        Image(
            painter = painterResource(spriteRes(spriteIndex)),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(top = 10.dp)
                .size(34.dp)
        )
        Text(
            text = name,
            color = InkPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.W700,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 6.dp, start = 2.dp, end = 2.dp)
                .fillMaxWidth()
        )
    }
}
