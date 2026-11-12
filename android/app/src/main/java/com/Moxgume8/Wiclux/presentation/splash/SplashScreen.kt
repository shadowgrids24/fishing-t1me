package com.Moxgume8.Wiclux.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Moxgume8.Wiclux.R
import com.Moxgume8.Wiclux.core.theme.AccentCyan
import com.Moxgume8.Wiclux.core.theme.IceMuted
import com.Moxgume8.Wiclux.core.theme.MintHighlight
import com.Moxgume8.Wiclux.core.theme.TextOnWater
import com.Moxgume8.Wiclux.presentation.app.rememberAppFactory
import kotlin.math.sin

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = rememberAppFactory())
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashScreen()
}

@Composable
private fun SplashScreen() {
    val transition = rememberInfiniteTransition(label = "splash")
    val rise by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rise"
    )
    val shaft by transition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shaft"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.22f,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF063A52).copy(alpha = 0.94f),
                            0.35f to Color(0xFF0A4A6B).copy(alpha = 0.92f),
                            0.70f to Color(0xFF00558B).copy(alpha = 0.90f),
                            1f to Color(0xFF022B3F).copy(alpha = 0.96f)
                        )
                    )
                )
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val auroraOne = Offset(width * 0.24f, height * 0.16f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AccentCyan.copy(alpha = 0.22f), Color.Transparent),
                    center = auroraOne,
                    radius = width * 0.72f
                ),
                radius = width * 0.72f,
                center = auroraOne
            )
            val auroraTwo = Offset(width * 0.80f, height * 0.80f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(MintHighlight.copy(alpha = 0.14f), Color.Transparent),
                    center = auroraTwo,
                    radius = width * 0.60f
                ),
                radius = width * 0.60f,
                center = auroraTwo
            )

            var shaftIndex = 0
            val shaftWidths = floatArrayOf(36f, 22f, 52f)
            while (shaftIndex < 3) {
                val x = width * (0.22f + shaftIndex * 0.28f)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = shaft),
                            Color.Transparent
                        )
                    ),
                    topLeft = Offset(x, 0f),
                    size = Size(shaftWidths[shaftIndex], height * 0.72f)
                )
                shaftIndex++
            }

            var bubbleIndex = 0
            while (bubbleIndex < 9) {
                val phase = (rise + bubbleIndex * 0.11f) % 1f
                val bubbleX = width * (0.08f + bubbleIndex * 0.1f) +
                    sin((phase + bubbleIndex) * 6.2831855f) * 14f
                val bubbleY = height * (1.02f - phase * 1.08f)
                val radius = 4f + (bubbleIndex % 4) * 2.4f
                drawCircle(
                    color = Color(0xFFD1E7EE).copy(alpha = 0.10f + (bubbleIndex % 3) * 0.08f),
                    radius = radius * 2.2f,
                    center = Offset(bubbleX, bubbleY)
                )
                bubbleIndex++
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(148.dp)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(108.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.title_app),
                    style = MaterialTheme.typography.displaySmall,
                    color = AccentCyan.copy(alpha = 0.30f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer { translationY = 5f }
                )
                Text(
                    text = stringResource(R.string.title_app),
                    style = MaterialTheme.typography.displaySmall,
                    color = TextOnWater,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.subtitle_app),
                style = MaterialTheme.typography.labelLarge,
                color = IceMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(44.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(176.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = AccentCyan,
                trackColor = Color.White.copy(alpha = 0.12f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = IceMuted.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}
