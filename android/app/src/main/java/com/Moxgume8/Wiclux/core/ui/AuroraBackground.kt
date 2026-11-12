package com.Moxgume8.Wiclux.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.Moxgume8.Wiclux.R
import com.Moxgume8.Wiclux.core.theme.AccentCyan
import com.Moxgume8.Wiclux.core.theme.MintHighlight

@Composable
fun AuroraBackground(
    scrim: List<Color>,
    modifier: Modifier = Modifier,
    artAlpha: Float = 0.3f,
    firstBlob: Color = AccentCyan,
    secondBlob: Color = MintHighlight,
    firstBlobAlpha: Float = 0.20f,
    secondBlobAlpha: Float = 0.14f,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = artAlpha,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(scrim))
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val firstCenter = Offset(width * 0.22f + drift * 46f, height * 0.18f)
            val firstRadius = width * 0.62f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(firstBlob.copy(alpha = firstBlobAlpha), Color.Transparent),
                    center = firstCenter,
                    radius = firstRadius
                ),
                radius = firstRadius,
                center = firstCenter
            )
            val secondCenter = Offset(width * 0.82f - drift * 34f, height * 0.74f)
            val secondRadius = width * 0.52f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(secondBlob.copy(alpha = secondBlobAlpha), Color.Transparent),
                    center = secondCenter,
                    radius = secondRadius
                ),
                radius = secondRadius,
                center = secondCenter
            )
        }
        content()
    }
}
