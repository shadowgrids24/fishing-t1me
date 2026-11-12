package com.Moxgume8.Wiclux.presentation.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Moxgume8.Wiclux.R
import com.Moxgume8.Wiclux.core.theme.AccentCyan
import com.Moxgume8.Wiclux.core.theme.AccentDeep
import com.Moxgume8.Wiclux.core.theme.IceMuted
import com.Moxgume8.Wiclux.core.theme.MintHighlight
import com.Moxgume8.Wiclux.core.theme.RarityGold
import com.Moxgume8.Wiclux.core.theme.TextOnWater
import com.Moxgume8.Wiclux.core.theme.WarnAmber
import com.Moxgume8.Wiclux.core.ui.AuroraBackground
import com.Moxgume8.Wiclux.core.ui.EmptyStateMessage
import com.Moxgume8.Wiclux.core.ui.PrimaryButton
import com.Moxgume8.Wiclux.core.ui.RarityTag
import com.Moxgume8.Wiclux.core.ui.SecondaryButton
import com.Moxgume8.Wiclux.core.ui.StarStrip
import com.Moxgume8.Wiclux.core.ui.StatCardSpec
import com.Moxgume8.Wiclux.core.ui.StatRow
import com.Moxgume8.Wiclux.core.ui.formatNumber
import com.Moxgume8.Wiclux.domain.model.TripResult
import com.Moxgume8.Wiclux.presentation.app.rememberAppFactory

@Composable
fun ResultRoute(
    trip: TripResult,
    onPlayAgain: () -> Unit,
    onLevelSelect: () -> Unit,
    onCollection: () -> Unit,
    onMenu: () -> Unit
) {
    val resultViewModel: ResultViewModel = viewModel(factory = rememberAppFactory())
    val state by resultViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(trip) {
        resultViewModel.bind(trip)
    }

    ResultScreen(
        state = state,
        onPlayAgain = onPlayAgain,
        onLevelSelect = onLevelSelect,
        onCollection = onCollection,
        onMenu = onMenu
    )
}

@Composable
private fun ResultScreen(
    state: ResultUiState,
    onPlayAgain: () -> Unit,
    onLevelSelect: () -> Unit,
    onCollection: () -> Unit,
    onMenu: () -> Unit
) {
    val scrim = if (state.win) {
        listOf(
            Color(0xFF0A4A6B).copy(alpha = 0.92f),
            Color(0xFF00A6CB).copy(alpha = 0.65f),
            Color(0xFF7FE8C5).copy(alpha = 0.20f)
        )
    } else {
        listOf(
            Color(0xFF04293C).copy(alpha = 0.95f),
            Color(0xFF1D6E96).copy(alpha = 0.60f),
            Color(0xFF063A52).copy(alpha = 0.95f)
        )
    }

    AuroraBackground(
        scrim = scrim,
        artAlpha = 0.26f,
        firstBlob = if (state.win) MintHighlight else WarnAmber,
        firstBlobAlpha = 0.18f,
        secondBlobAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (state.win) {
                    stringResource(R.string.result_win)
                } else {
                    stringResource(R.string.result_loss)
                },
                style = MaterialTheme.typography.displaySmall,
                color = if (state.win) MintHighlight else WarnAmber,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.levelNameRes != 0) {
                Text(
                    text = stringResource(
                        R.string.result_spot,
                        state.levelId,
                        stringResource(state.levelNameRes)
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = IceMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(
                    R.string.result_weight,
                    formatNumber(state.grams),
                    formatNumber(state.targetGrams)
                ),
                style = MaterialTheme.typography.titleMedium,
                color = TextOnWater,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            StarStrip(
                earned = state.stars,
                animateIndex = if (state.stars > 0) state.stars - 1 else null,
                starSize = 24.dp,
                emptyColor = Color.White.copy(alpha = 0.22f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            val cards = mutableListOf<StatCardSpec>()
            if (state.grams > 0) {
                cards.add(
                    StatCardSpec(
                        label = stringResource(R.string.stat_weight),
                        value = formatNumber(state.grams),
                        accent = MintHighlight
                    )
                )
            }
            if (state.catchCount > 0) {
                cards.add(
                    StatCardSpec(
                        label = stringResource(R.string.stat_catches),
                        value = formatNumber(state.catchCount),
                        accent = AccentCyan
                    )
                )
            }
            if (state.bestStreak > 0) {
                cards.add(
                    StatCardSpec(
                        label = stringResource(R.string.stat_best_streak),
                        value = formatNumber(state.bestStreak),
                        accent = RarityGold
                    )
                )
            }
            StatRow(cards = cards, onDark = true)

            Spacer(modifier = Modifier.height(14.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (state.rows.isEmpty()) {
                    EmptyStateMessage(
                        text = stringResource(R.string.result_empty),
                        color = IceMuted
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.rows, key = { it.key }) { row ->
                            CatchRow(row = row)
                        }
                    }
                }
            }

            if (state.unlockedNext) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MintHighlight.copy(alpha = 0.18f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.next_spot_open),
                        style = MaterialTheme.typography.labelSmall,
                        color = MintHighlight
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            PrimaryButton(
                text = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(),
                iconRes = R.drawable.ic_hook
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.level_select),
                    onClick = onLevelSelect,
                    modifier = Modifier.weight(1f),
                    contentColor = TextOnWater,
                    borderColor = if (state.unlockedNext) MintHighlight else AccentCyan.copy(alpha = 0.55f)
                )
                SecondaryButton(
                    text = stringResource(R.string.collection),
                    onClick = onCollection,
                    modifier = Modifier.weight(1f),
                    contentColor = TextOnWater
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth(),
                height = 48.dp,
                fill = Color.Transparent,
                borderColor = Color.White.copy(alpha = 0.30f),
                contentColor = IceMuted
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CatchRow(row: ResultCatchRow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(row.spriteRes),
            contentDescription = stringResource(row.nameRes),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color(row.tint), BlendMode.Modulate),
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(row.nameRes),
                style = MaterialTheme.typography.titleSmall,
                color = TextOnWater
            )
            Spacer(modifier = Modifier.height(2.dp))
            RarityTag(rarity = row.rarity)
        }
        if (row.isNew) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MintHighlight.copy(alpha = 0.20f))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = MintHighlight
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = stringResource(R.string.grams_value, formatNumber(row.grams)),
            style = MaterialTheme.typography.titleSmall,
            color = AccentCyan
        )
    }
}
