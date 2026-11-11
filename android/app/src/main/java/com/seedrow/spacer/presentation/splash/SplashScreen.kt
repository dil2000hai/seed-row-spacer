package com.seedrow.spacer.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedrow.spacer.R
import com.seedrow.spacer.core.di.ServiceLocator
import com.seedrow.spacer.core.theme.SplashOverlay
import com.seedrow.spacer.core.theme.SproutGreen
import com.seedrow.spacer.core.ui.AppBackdrop
import com.seedrow.spacer.core.ui.spriteRes

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    SplashScreen(finished = finished, onFinished = onFinished)
}

@Composable
fun SplashScreen(finished: Boolean, onFinished: () -> Unit) {
    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        revealed = true
    }

    val plateScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.86f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 260f)
    )
    val plateAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(420)
    )

    val transition = rememberInfiniteTransition()
    val driftOne by transition.animateFloat(
        initialValue = -9f,
        targetValue = 9f,
        animationSpec = infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val driftTwo by transition.animateFloat(
        initialValue = 9f,
        targetValue = -9f,
        animationSpec = infiniteRepeatable(tween(3100, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val driftThree by transition.animateFloat(
        initialValue = -7f,
        targetValue = 11f,
        animationSpec = infiniteRepeatable(tween(3600, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val glow by transition.animateFloat(
        initialValue = 120f,
        targetValue = 138f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    val dashPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart)
    )

    AppBackdrop(overlay = SplashOverlay) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            SplashSpark(spriteIndex = 1, drift = driftOne, alpha = 0.26f, x = (-118).dp, y = (-180).dp)
            SplashSpark(spriteIndex = 2, drift = driftTwo, alpha = 0.20f, x = 122.dp, y = (-128).dp)
            SplashSpark(spriteIndex = 3, drift = driftThree, alpha = 0.30f, x = (-96).dp, y = 176.dp)

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(glow.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0x337FA46B), Color(0x00000000))
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(196.dp)
                            .graphicsLayer {
                                scaleX = plateScale
                                scaleY = plateScale
                                alpha = plateAlpha
                            }
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0x26F6F3E7))
                            .border(
                                BorderStroke(1.5.dp, Color(0x4DD8E2C6)),
                                RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.app_title),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color(0xFFF6F3E7),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 28.dp)
                        .alpha(plateAlpha)
                )

                Canvas(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .width(190.dp)
                        .height(2.dp)
                ) {
                    drawLine(
                        color = Color(0x66D8E2C6),
                        start = Offset(0f, size.height / 2f),
                        end = Offset(size.width, size.height / 2f),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(10f, 10f),
                            dashPhase
                        )
                    )
                }

                Text(
                    text = stringResource(R.string.tagline),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD8E2C6),
                    fontSize = 12.sp,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .alpha(0.82f)
                )

                LinearProgressIndicator(
                    color = SproutGreen,
                    trackColor = Color(0x33D8E2C6),
                    modifier = Modifier
                        .padding(top = 34.dp)
                        .width(168.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD8E2C6),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .alpha(0.7f)
                )
            }
        }
    }
}

@Composable
private fun SplashSpark(
    spriteIndex: Int,
    drift: Float,
    alpha: Float,
    x: androidx.compose.ui.unit.Dp,
    y: androidx.compose.ui.unit.Dp
) {
    Image(
        painter = painterResource(spriteRes(spriteIndex)),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .offset(x = x, y = y + drift.dp)
            .size(34.dp)
            .alpha(alpha)
    )
}
