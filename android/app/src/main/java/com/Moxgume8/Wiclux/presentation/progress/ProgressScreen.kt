package com.Moxgume8.Wiclux.presentation.progress

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Moxgume8.Wiclux.R
import com.Moxgume8.Wiclux.core.theme.AccentCyan
import com.Moxgume8.Wiclux.core.theme.AccentDeep
import com.Moxgume8.Wiclux.core.theme.IceCard
import com.Moxgume8.Wiclux.core.theme.RarityCommon
import com.Moxgume8.Wiclux.core.theme.RarityGold
import com.Moxgume8.Wiclux.core.theme.TextOnWater
import com.Moxgume8.Wiclux.core.theme.TextSecondary
import com.Moxgume8.Wiclux.core.ui.AuroraBackground
import com.Moxgume8.Wiclux.core.ui.EmptyStateMessage
import com.Moxgume8.Wiclux.core.ui.ScreenHeader
import com.Moxgume8.Wiclux.core.ui.StarStrip
import com.Moxgume8.Wiclux.core.ui.StatCardSpec
import com.Moxgume8.Wiclux.core.ui.StatRow
import com.Moxgume8.Wiclux.core.ui.formatNumber
import com.Moxgume8.Wiclux.presentation.app.rememberAppFactory

@Composable
fun ProgressRoute(onMenu: () -> Unit) {
    val progressViewModel: ProgressViewModel = viewModel(factory = rememberAppFactory())
    val state by progressViewModel.state.collectAsStateWithLifecycle()
    ProgressScreen(state = state, onMenu = onMenu)
}

@Composable
private fun ProgressScreen(state: ProgressUiState, onMenu: () -> Unit) {
    AuroraBackground(
        scrim = listOf(
            Color(0xFFE8F4F8).copy(alpha = 0.93f),
            Color(0xFFB8DBE5).copy(alpha = 0.95f)
        ),
        artAlpha = 0.30f,
        firstBlobAlpha = 0.15f,
        secondBlobAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.progress_title),
                onMenu = onMenu
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 26.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    HeroTile(
                        weight = state.lifetimeWeightG,
                        trips = state.trips
                    )
                }

                item {
                    val topRow = mutableListOf<StatCardSpec>()
                    if (state.bestTripWeightG > 0) {
                        topRow.add(
                            StatCardSpec(
                                label = stringResource(R.string.stat_best_trip),
                                value = formatNumber(state.bestTripWeightG),
                                accent = AccentDeep
                            )
                        )
                    }
                    if (state.speciesFound > 0) {
                        topRow.add(
                            StatCardSpec(
                                label = stringResource(R.string.stat_species_found),
                                value = stringResource(
                                    R.string.species_ratio,
                                    state.speciesFound,
                                    state.totalSpecies
                                ),
                                accent = AccentCyan
                            )
                        )
                    }
                    StatRow(cards = topRow)
                }

                item {
                    val bottomRow = mutableListOf<StatCardSpec>()
                    if (state.bestStreak > 0) {
                        bottomRow.add(
                            StatCardSpec(
                                label = stringResource(R.string.stat_best_streak),
                                value = formatNumber(state.bestStreak),
                                accent = RarityGold
                            )
                        )
                    }
                    if (state.rareCatches > 0) {
                        bottomRow.add(
                            StatCardSpec(
                                label = stringResource(R.string.stat_rare_catches),
                                value = formatNumber(state.rareCatches),
                                accent = RarityGold
                            )
                        )
                    }
                    StatRow(cards = bottomRow)
                }

                item {
                    Text(
                        text = stringResource(R.string.progress_spots),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }

                items(state.spots, key = { it.levelId }) { spot ->
                    SpotProgressCard(spot = spot)
                }

                item {
                    Text(
                        text = stringResource(R.string.progress_rarity),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }

                item {
                    if (state.totalCatches > 0) {
                        RaritySplit(
                            common = state.commonCount,
                            uncommon = state.uncommonCount,
                            rare = state.rareCount
                        )
                    } else {
                        EmptyStateMessage(
                            text = stringResource(R.string.progress_empty),
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroTile(weight: Int, trips: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(AccentCyan, AccentDeep)))
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.progress_total_weight),
            style = MaterialTheme.typography.labelMedium,
            color = TextOnWater.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.grams_value, formatNumber(weight)),
            style = MaterialTheme.typography.headlineMedium,
            color = TextOnWater
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.progress_trips, trips),
            style = MaterialTheme.typography.labelSmall,
            color = TextOnWater.copy(alpha = 0.78f)
        )
    }
}

@Composable
private fun SpotProgressCard(spot: SpotProgressRow) {
    val fraction = if (spot.targetGrams <= 0) {
        0f
    } else {
        (spot.bestWeightG.toFloat() / spot.targetGrams.toFloat()).coerceIn(0f, 1f)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.76f))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(spot.nameRes),
                style = MaterialTheme.typography.titleSmall,
                color = if (spot.unlocked) AccentDeep else TextSecondary,
                modifier = Modifier.weight(1f)
            )
            if (spot.unlocked) {
                StarStrip(earned = spot.stars, starSize = 14.dp)
            } else {
                Text(
                    text = stringResource(R.string.progress_spot_locked),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.45f)
                )
            }
        }
        if (spot.unlocked) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(IceCard)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(AccentCyan)
                )
            }
            if (spot.bestWeightG > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.levels_best, formatNumber(spot.bestWeightG)),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun RaritySplit(common: Int, uncommon: Int, rare: Int) {
    val total = (common + uncommon + rare).coerceAtLeast(1)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(IceCard)
        ) {
            if (common > 0) {
                Box(
                    modifier = Modifier
                        .weight(common.toFloat() / total)
                        .fillMaxSize()
                        .background(RarityCommon)
                )
            }
            if (uncommon > 0) {
                Box(
                    modifier = Modifier
                        .weight(uncommon.toFloat() / total)
                        .fillMaxSize()
                        .background(AccentCyan)
                )
            }
            if (rare > 0) {
                Box(
                    modifier = Modifier
                        .weight(rare.toFloat() / total)
                        .fillMaxSize()
                        .background(RarityGold)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SplitLabel(labelRes = R.string.rarity_common, value = common, color = RarityCommon)
            Spacer(modifier = Modifier.width(8.dp))
            SplitLabel(labelRes = R.string.rarity_uncommon, value = uncommon, color = AccentCyan)
            Spacer(modifier = Modifier.width(8.dp))
            SplitLabel(labelRes = R.string.rarity_rare, value = rare, color = RarityGold)
        }
    }
}

@Composable
private fun SplitLabel(labelRes: Int, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = formatNumber(value),
            style = MaterialTheme.typography.titleSmall,
            color = color,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
