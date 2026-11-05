package com.seedrow.spacer.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.seedrow.spacer.core.config.GameConfig
import com.seedrow.spacer.core.theme.SageBorder
import com.seedrow.spacer.core.theme.SoilBrush
import com.seedrow.spacer.core.theme.SoilEdge
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun BedCanvas(
    lengthCm: Int,
    widthCm: Int,
    inRowCm: Int,
    rowPitchCm: Float,
    rows: Int,
    columns: Int,
    spriteIndex: Int,
    revealProgress: Float,
    shiftFactor: Float,
    oddRowShorter: Boolean,
    modifier: Modifier = Modifier
) {
    val sprite: ImageBitmap = ImageBitmap.imageResource(spriteRes(spriteIndex))
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val frame = (GameConfig.BOARD_PAD_DP + GameConfig.BOARD_BORDER_DP).dp
        val boardMaxW = minOf(maxWidth, GameConfig.BOARD_MAX_W_DP.dp)
        val innerMaxW = (boardMaxW - frame * 2).coerceAtLeast(48.dp)
        val innerMaxH = (maxHeight - frame * 2).coerceAtLeast(48.dp)
        val safeLength = max(lengthCm, 1)
        val safeWidth = max(widthCm, 1)

        var dpPerCm = innerMaxW.value / safeLength
        if (safeWidth * dpPerCm > innerMaxH.value) {
            dpPerCm = innerMaxH.value / safeWidth
        }
        val boardW = (safeLength * dpPerCm).dp + frame * 2
        val boardH = (safeWidth * dpPerCm).dp + frame * 2
        val shape = RoundedCornerShape(18.dp)

        Box(
            modifier = Modifier
                .size(boardW, boardH)
                .shadow(8.dp, shape)
                .clip(shape)
        ) {
            Canvas(modifier = Modifier.size(boardW, boardH)) {
                val corner = CornerRadius(18.dp.toPx())
                drawRoundRect(brush = SoilBrush, cornerRadius = corner)
                drawRoundRect(
                    color = SoilEdge,
                    cornerRadius = corner,
                    style = Stroke(width = GameConfig.BOARD_BORDER_DP.dp.toPx())
                )

                val framePx = frame.toPx()
                val pxPerCm = dpPerCm * density
                val edgeCm = inRowCm / 2f
                val dash = PathEffect.dashPathEffect(floatArrayOf(7f, 7f), 0f)
                val guideColor = SageBorder.copy(alpha = 0.55f)
                val firstRowY = framePx + edgeCm * pxPerCm
                val firstColX = framePx + edgeCm * pxPerCm

                drawLine(
                    color = guideColor,
                    start = Offset(framePx, firstRowY),
                    end = Offset(size.width - framePx, firstRowY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = dash
                )
                drawLine(
                    color = guideColor,
                    start = Offset(firstColX, framePx),
                    end = Offset(firstColX, size.height - framePx),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = dash
                )

                if (rows <= 0 || columns <= 0 || inRowCm <= 0) return@Canvas

                val baseSprite = (inRowCm * pxPerCm * 1.25f)
                    .coerceIn(8.dp.toPx(), 40.dp.toPx())
                var drawn = 0
                for (row in 0 until rows) {
                    val reveal = (revealProgress - row).coerceIn(0f, 1f)
                    if (reveal <= 0f) continue
                    val shifted = row % 2 == 1
                    val rowColumns = if (oddRowShorter && shifted) max(columns - 1, 0) else columns
                    val shiftCm = if (shifted) shiftFactor * inRowCm / 2f else 0f
                    val centerY = framePx + (edgeCm + row * rowPitchCm) * pxPerCm
                    val spriteSize = baseSprite * (0.4f + 0.6f * reveal)
                    if (spriteSize < 2f) continue
                    val side = spriteSize.roundToInt()
                    for (column in 0 until rowColumns) {
                        val centerX = framePx + (edgeCm + column * inRowCm + shiftCm) * pxPerCm
                        drawImage(
                            image = sprite,
                            dstOffset = IntOffset(
                                (centerX - spriteSize / 2f).roundToInt(),
                                (centerY - spriteSize / 2f).roundToInt()
                            ),
                            dstSize = IntSize(side, side),
                            alpha = reveal
                        )
                        drawn++
                        if (drawn >= MAX_DRAWN_PLANTS) return@Canvas
                    }
                }
            }
        }
    }
}

private const val MAX_DRAWN_PLANTS = 900
