package com.Moxgume8.Wiclux.presentation.levels

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Moxgume8.Wiclux.R
import com.Moxgume8.Wiclux.core.theme.AccentCyan
import com.Moxgume8.Wiclux.core.theme.AccentDeep
import com.Moxgume8.Wiclux.core.theme.IceBorder
import com.Moxgume8.Wiclux.core.theme.IceCard
import com.Moxgume8.Wiclux.core.theme.IceMuted
import com.Moxgume8.Wiclux.core.theme.TextSecondary
import com.Moxgume8.Wiclux.core.ui.AuroraBackground
import com.Moxgume8.Wiclux.core.ui.PrimaryButton
import com.Moxgume8.Wiclux.core.ui.ScreenHeader
import com.Moxgume8.Wiclux.core.ui.StarStrip
import com.Moxgume8.Wiclux.core.ui.formatNumber
import com.Moxgume8.Wiclux.presentation.app.rememberAppFactory

@Composable
fun LevelSelectRoute(onStart: (Int) -> Unit, onMenu: () -> Unit) {
    val levelViewModel: LevelSelectViewModel = viewModel(factory = rememberAppFactory())
    val state by levelViewModel.state.collectAsStateWithLifecycle()

    LevelSelectScreen(
        state = state,
        onSelect = { levelId -> levelViewModel.select(levelId) },
        onStart = onStart,
        onMenu = onMenu
    )
}

@Composable
private fun LevelSelectScreen(
    state: LevelSelectUiState,
    onSelect: (Int) -> Unit,
    onStart: (Int) -> Unit,
    onMenu: () -> Unit
) {
    AuroraBackground(
        scrim = listOf(
            Color(0xFFE8F4F8).copy(alpha = 0.92f),
            Color(0xFFB8DBE5).copy(alpha = 0.96f)
        ),
        artAlpha = 0.30f,
        firstBlobAlpha = 0.16f,
        secondBlobAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.levels_title),
                onMenu = onMenu,
                trailing = {
                    Text(
                        text = stringResource(R.string.levels_unlocked, state.unlocked, state.total),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        textAlign = TextAlign.End
                    )
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.spots, key = { it.id }) { spot ->
                    SpotCard(
                        spot = spot,
                        selected = state.selectedId == spot.id,
                        maxDepth = 16,
                        onSelect = { onSelect(spot.id) },
                        onStart = { onStart(spot.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.levels_footer),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SpotCard(
    spot: SpotRow,
    selected: Boolean,
    maxDepth: Int,
    onSelect: () -> Unit,
    onStart: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    val scale by animateFloatAsState(
        targetValue = if (selected && spot.unlocked) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 420f),
        label = "spotScale"
    )
    val borderColor = when {
        !spot.unlocked -> IceBorder.copy(alpha = 0.5f)
        selected -> AccentCyan
        else -> IceMuted.copy(alpha = 0.6f)
    }
    val stateText = if (spot.unlocked) {
        stringResource(R.string.state_unlocked)
    } else {
        stringResource(R.string.state_locked)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (spot.unlocked) 1f else 0.46f
            }
            .clip(shape)
            .background(Color.White.copy(alpha = 0.78f))
            .border(if (selected && spot.unlocked) 2.dp else 1.dp, borderColor, shape)
            .clickable(enabled = spot.unlocked, onClick = onSelect)
            .semantics { stateDescription = stateText }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DepthBadge(depth = spot.depthM, maxDepth = maxDepth, locked = !spot.unlocked)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(spot.nameRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentDeep
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = stringResource(spot.hintRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(5.dp))
                if (spot.unlocked) {
                    Text(
                        text = stringResource(R.string.levels_target, formatNumber(spot.targetGrams)),
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentCyan
                    )
                } else {
                    Text(
                        text = stringResource(R.string.levels_locked_hint, spot.id - 1),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            StarStrip(earned = spot.stars, starSize = 15.dp)
        }

        if (spot.bestWeightG > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.levels_best, formatNumber(spot.bestWeightG)),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        if (selected && spot.unlocked) {
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = stringResource(R.string.start_level),
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                height = 48.dp,
                iconRes = R.drawable.ic_hook
            )
        }
    }
}

@Composable
private fun DepthBadge(depth: Int, maxDepth: Int, locked: Boolean) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(IceCard.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        if (locked) {
            Icon(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Canvas(modifier = Modifier.size(58.dp)) {
                val stroke = Stroke(width = 6f, cap = StrokeCap.Round)
                drawArc(
                    color = IceBorder,
                    startAngle = 130f,
                    sweepAngle = 280f,
                    useCenter = false,
                    style = stroke
                )
                val fraction = (depth.toFloat() / maxDepth.toFloat()).coerceIn(0.08f, 1f)
                drawArc(
                    color = AccentCyan,
                    startAngle = 130f,
                    sweepAngle = 280f * fraction,
                    useCenter = false,
                    style = stroke
                )
            }
            Text(
                text = stringResource(R.string.levels_depth, depth),
                style = MaterialTheme.typography.titleSmall,
                color = AccentDeep
            )
        }
    }
}
