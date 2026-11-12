package com.Moxgume8.Wiclux.presentation.game

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Moxgume8.Wiclux.R
import com.Moxgume8.Wiclux.core.config.GameConfig
import com.Moxgume8.Wiclux.core.theme.AccentCyan
import com.Moxgume8.Wiclux.core.theme.AccentDeep
import com.Moxgume8.Wiclux.core.theme.IceMuted
import com.Moxgume8.Wiclux.core.theme.IceSurface
import com.Moxgume8.Wiclux.core.theme.MintHighlight
import com.Moxgume8.Wiclux.core.theme.RarityGold
import com.Moxgume8.Wiclux.core.theme.WaterMid
import com.Moxgume8.Wiclux.core.ui.PrimaryButton
import com.Moxgume8.Wiclux.core.ui.formatNumber
import com.Moxgume8.Wiclux.domain.game.LakePhase
import com.Moxgume8.Wiclux.domain.game.LakeView
import com.Moxgume8.Wiclux.domain.model.TripResult
import com.Moxgume8.Wiclux.presentation.app.rememberAppFactory
import com.Moxgume8.Wiclux.presentation.dialog.PauseDialog
import kotlin.math.sin

@Composable
fun GameRoute(
    levelId: Int,
    onFinished: (TripResult) -> Unit,
    onMenu: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = rememberAppFactory())
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(levelId) {
        gameViewModel.prepare(levelId)
    }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onFinished(event.trip)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }

    GameScreen(
        state = state,
        onTap = { gameViewModel.tapLake() },
        onPause = { gameViewModel.setPaused(true) },
        onResize = { width, height -> gameViewModel.resize(width, height) }
    )

    if (state.paused) {
        PauseDialog(
            onResume = { gameViewModel.setPaused(false) },
            onMenu = onMenu
        )
    }
}

@Composable
private fun GameScreen(
    state: GameUiState,
    onTap: () -> Unit,
    onPause: () -> Unit,
    onResize: (Float, Float) -> Unit
) {
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.shakeTick) {
        if (state.shakeTick > 0) {
            shake.animateTo(1f, tween(durationMillis = 90, easing = LinearEasing))
            shake.animateTo(0f, tween(durationMillis = 90, easing = LinearEasing))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.14f,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFFBFE4EF).copy(alpha = 0.92f),
                            0.18f to Color(0xFF5FA8C6).copy(alpha = 0.92f),
                            0.46f to Color(0xFF1D6E96).copy(alpha = 0.94f),
                            0.74f to Color(0xFF0A4A6B).copy(alpha = 0.96f),
                            1f to Color(0xFF04293C).copy(alpha = 0.98f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            GameHeader(state = state, onPause = onPause)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .graphicsLayer { translationX = shake.value * 6f }
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val lakeWidth = maxWidth.value
                    val lakeHeight = maxHeight.value - GameConfig.CONTROL_BAR_RESERVE_DP

                    LaunchedEffect(lakeWidth, lakeHeight) {
                        onResize(lakeWidth, lakeHeight)
                    }

                    LakeSurface(
                        state = state,
                        onTap = onTap,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                FloatingControlBar(
                    state = state,
                    onAction = onTap,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun GameHeader(state: GameUiState, onPause: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color(0xFF06364E).copy(alpha = 0.55f))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.10f))
                .clickable(onClick = onPause),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.icon_pause),
                contentDescription = stringResource(R.string.cd_pause),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.levelNameRes != 0) {
                Text(
                    text = stringResource(state.levelNameRes),
                    style = MaterialTheme.typography.labelLarge,
                    color = IceMuted,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(
                    R.string.keepnet_readout,
                    formatNumber(state.grams),
                    formatNumber(state.targetGrams)
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MintHighlight,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
        }

        val linesText = stringResource(R.string.lines_state, state.linesLeft, state.maxLines)
        Row(
            modifier = Modifier
                .widthIn(min = 48.dp)
                .semantics { stateDescription = linesText },
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until state.maxLines) {
                val alive = index < state.linesLeft
                Icon(
                    painter = painterResource(R.drawable.ic_line_knot),
                    contentDescription = null,
                    tint = if (alive) MintHighlight else IceMuted.copy(alpha = 0.22f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun LakeSurface(
    state: GameUiState,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lake = state.lake
    val transition = rememberInfiniteTransition(label = "lake")
    val ringPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring"
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onTap
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val density = size.width / (if (lake != null && lake.width > 0f) lake.width else 1f)
            val ceiling = 46f * density

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(IceSurface.copy(alpha = 0.92f), IceMuted.copy(alpha = 0.55f)),
                    startY = 0f,
                    endY = ceiling
                ),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, ceiling)
            )

            var crack = 0
            while (crack < 5) {
                val startX = size.width * (0.08f + crack * 0.2f)
                drawLine(
                    color = Color.White.copy(alpha = 0.55f),
                    start = Offset(startX, ceiling * 0.2f),
                    end = Offset(startX + 26f * density, ceiling * 0.92f),
                    strokeWidth = 1.5f * density,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.38f),
                    start = Offset(startX + 26f * density, ceiling * 0.92f),
                    end = Offset(startX + 6f * density, ceiling * 1.5f),
                    strokeWidth = 1.2f * density,
                    cap = StrokeCap.Round
                )
                crack++
            }

            if (lake != null && lake.height > 0f) {
                val bandAlpha = if (state.settings.highContrast) 0.09f else 0.04f
                for (lane in lake.lanes) {
                    val top = lane.topFraction * lake.height * density
                    val bottom = lane.bottomFraction * lake.height * density
                    drawRect(
                        color = Color.White.copy(alpha = bandAlpha),
                        topLeft = Offset(0f, top),
                        size = Size(size.width, bottom - top)
                    )
                }

                for (shard in lake.shards) {
                    val shardSize = shard.sizeDp * density
                    drawRect(
                        color = Color(0xFFDCEFF5).copy(alpha = 0.70f),
                        topLeft = Offset(shard.left * density, shard.top * density),
                        size = Size(shardSize, shardSize * 0.46f)
                    )
                }

                val ripple = lake.ripple
                if (ripple != null) {
                    val ringColor = if (ripple.golden) RarityGold else MintHighlight
                    val centerX = ripple.centerX * density
                    val baseY = ceiling * 0.55f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ringColor.copy(alpha = 0.18f), Color.Transparent),
                            center = Offset(centerX, baseY),
                            radius = 64f * density
                        ),
                        radius = 64f * density,
                        center = Offset(centerX, baseY)
                    )
                    var ringIndex = 0
                    while (ringIndex < 3) {
                        val phase = (ringPhase + ringIndex / 3f) % 1f
                        val radius = (10f + phase * 36f) * density
                        val alpha = if (ripple.open) (1f - phase) * 0.9f else (1f - phase) * 0.35f
                        drawCircle(
                            color = ringColor.copy(alpha = alpha),
                            radius = radius,
                            center = Offset(centerX, baseY),
                            style = Stroke(width = 2f * density)
                        )
                        ringIndex++
                    }
                }

                val hookX = lake.hookX * density
                val hookY = lake.hookY * density
                drawLine(
                    color = IceSurface.copy(alpha = 0.70f),
                    start = Offset(hookX, 0f),
                    end = Offset(hookX, hookY),
                    strokeWidth = 2f * density
                )

                if (state.settings.aimAssist && lake.phase == LakePhase.IDLE) {
                    drawLine(
                        color = MintHighlight.copy(alpha = 0.35f),
                        start = Offset(hookX, hookY),
                        end = Offset(hookX, lake.height * density),
                        strokeWidth = 2f * density,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(8f * density, 10f * density)
                        )
                    )
                }

                drawLine(
                    color = IceSurface.copy(alpha = 0.85f),
                    start = Offset(lake.rodX * density - 22f * density, 6f * density),
                    end = Offset(lake.rodX * density + 10f * density, 2f * density),
                    strokeWidth = 3f * density,
                    cap = StrokeCap.Round
                )
            }
        }

        if (lake != null) {
            for (fish in lake.fish) {
                key(fish.id) {
                    Image(
                        painter = painterResource(fish.spriteRes),
                        contentDescription = stringResource(fish.nameRes),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color(fish.tint), BlendMode.Modulate),
                        modifier = Modifier
                            .offset(x = fish.left.dp, y = fish.top.dp)
                            .size(fish.sizeDp.dp)
                            .graphicsLayer {
                                scaleX = if (fish.facingRight) 1f else -1f
                                alpha = fish.alpha
                            }
                    )
                }
            }

            Icon(
                painter = painterResource(R.drawable.ic_hook),
                contentDescription = null,
                tint = IceSurface,
                modifier = Modifier
                    .offset(x = (lake.hookX - 11f).dp, y = (lake.hookY - 11f).dp)
                    .size(22.dp)
            )

            if (lake.phase == LakePhase.REELING && lake.hookedSpriteRes != 0) {
                Image(
                    painter = painterResource(lake.hookedSpriteRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color(lake.hookedTint), BlendMode.Modulate),
                    modifier = Modifier
                        .offset(
                            x = (lake.hookX - lake.hookedSizeDp * 0.5f).dp,
                            y = (lake.hookY + 6f).dp
                        )
                        .size(lake.hookedSizeDp.dp)
                )
            }
        }

        val flash = state.flash
        if (flash != null) {
            key(flash.id) {
                CatchBurst(
                    grams = flash.grams,
                    multiplier = flash.multiplier,
                    golden = flash.golden,
                    modifier = Modifier.offset(x = (flash.x - 54f).dp, y = (flash.y - 34f).dp)
                )
            }
        }
    }
}

@Composable
private fun CatchBurst(
    grams: Int,
    multiplier: Float,
    golden: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(durationMillis = 600, easing = LinearEasing))
    }
    val accent = if (golden) RarityGold else MintHighlight

    Box(modifier = modifier.size(width = 108.dp, height = 72.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var bubble = 0
            while (bubble < 12) {
                val offsetAngle = bubble * 0.5236f
                val travel = progress.value * 46f
                val x = size.width * 0.5f + sin(offsetAngle) * 22f
                val y = size.height * 0.7f - travel - bubble * 1.4f
                drawCircle(
                    color = accent.copy(alpha = (1f - progress.value) * 0.65f),
                    radius = 3f + (bubble % 3),
                    center = Offset(x, y)
                )
                bubble++
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    translationY = -progress.value * 28f
                    alpha = 1f - progress.value * 0.85f
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.plus_grams, formatNumber(grams)),
                style = MaterialTheme.typography.titleMedium,
                color = accent
            )
            if (multiplier >= 2f) {
                Text(
                    text = stringResource(R.string.multiplier_label, formatMultiplier(multiplier)),
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }
        }
    }
}

@Composable
private fun FloatingControlBar(
    state: GameUiState,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lake: LakeView? = state.lake
    val dropping = lake != null && lake.phase == LakePhase.DROPPING
    val actionEnabled = when {
        state.finished || state.paused -> false
        lake == null -> false
        lake.phase == LakePhase.IDLE -> true
        lake.phase == LakePhase.DROPPING -> lake.canJerk
        else -> false
    }
    val actionText = if (dropping) stringResource(R.string.jerk) else stringResource(R.string.drop)
    val gradient = if (dropping) {
        listOf(MintHighlight, AccentCyan)
    } else {
        listOf(AccentCyan, AccentDeep)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF063A52).copy(alpha = 0.72f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.game_hint),
            style = MaterialTheme.typography.labelSmall,
            color = if (state.hintBright) MintHighlight else IceMuted.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (state.streak >= 1) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MintHighlight.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.streak_pill, state.streak),
                        style = MaterialTheme.typography.labelSmall,
                        color = MintHighlight
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            PrimaryButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.widthIn(min = 148.dp),
                enabled = actionEnabled,
                height = 56.dp,
                gradient = gradient,
                shadowColor = WaterMid
            )
        }
    }
}

private fun formatMultiplier(value: Float): String {
    val scaled = (value * 10f).toInt()
    return if (scaled % 10 == 0) "${scaled / 10}" else "${scaled / 10}.${scaled % 10}"
}
