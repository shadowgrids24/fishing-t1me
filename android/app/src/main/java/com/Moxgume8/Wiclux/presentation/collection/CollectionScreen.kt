package com.Moxgume8.Wiclux.presentation.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Moxgume8.Wiclux.R
import com.Moxgume8.Wiclux.core.theme.AccentCyan
import com.Moxgume8.Wiclux.core.theme.AccentDeep
import com.Moxgume8.Wiclux.core.theme.IceCard
import com.Moxgume8.Wiclux.core.theme.IceMuted
import com.Moxgume8.Wiclux.core.theme.TextSecondary
import com.Moxgume8.Wiclux.core.ui.AuroraBackground
import com.Moxgume8.Wiclux.core.ui.EmptyStateMessage
import com.Moxgume8.Wiclux.core.ui.RarityTag
import com.Moxgume8.Wiclux.core.ui.ScreenHeader
import com.Moxgume8.Wiclux.core.ui.formatNumber
import com.Moxgume8.Wiclux.core.ui.formatPercent
import com.Moxgume8.Wiclux.core.ui.rarityColor
import com.Moxgume8.Wiclux.domain.model.Lane
import com.Moxgume8.Wiclux.presentation.app.rememberAppFactory

@Composable
fun CollectionRoute(onMenu: () -> Unit) {
    val collectionViewModel: CollectionViewModel = viewModel(factory = rememberAppFactory())
    val state by collectionViewModel.state.collectAsStateWithLifecycle()
    CollectionScreen(state = state, onMenu = onMenu)
}

@Composable
private fun CollectionScreen(state: CollectionUiState, onMenu: () -> Unit) {
    AuroraBackground(
        scrim = listOf(
            Color(0xFFE8F4F8).copy(alpha = 0.93f),
            Color(0xFFD1E7EE).copy(alpha = 0.95f)
        ),
        artAlpha = 0.30f,
        firstBlobAlpha = 0.14f,
        secondBlobAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.collection_title),
                onMenu = onMenu,
                trailing = {
                    Text(
                        text = stringResource(R.string.species_ratio, state.found, state.total),
                        style = MaterialTheme.typography.titleSmall,
                        color = AccentCyan,
                        textAlign = TextAlign.End
                    )
                }
            )

            if (state.found == 0) {
                EmptyStateMessage(
                    text = stringResource(R.string.collection_empty),
                    color = TextSecondary
                )
            }

            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val cellWidth: Dp = (maxWidth - 32.dp - 12.dp) / 2
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.entries, key = { it.speciesId }) { entry ->
                        SpeciesCard(entry = entry, cardHeight = cellWidth * 1.18f)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                LinearProgressIndicator(
                    progress = { completion(state.found, state.total) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AccentCyan,
                    trackColor = IceCard
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(
                        R.string.collection_percent,
                        formatPercent(state.found, state.total)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun SpeciesCard(entry: CollectionEntry, cardHeight: Dp) {
    val shape = RoundedCornerShape(22.dp)
    val accent = rarityColor(entry.rarity)
    val laneLabel = when (entry.lane) {
        Lane.SHALLOW -> stringResource(R.string.lane_shallow)
        Lane.MID -> stringResource(R.string.lane_mid)
        Lane.DEEP -> stringResource(R.string.lane_deep)
    }
    val lockedState = stringResource(R.string.state_locked)
    val unlockedState = stringResource(R.string.state_unlocked)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clip(shape)
            .background(if (entry.unlocked) Color.White.copy(alpha = 0.80f) else IceCard.copy(alpha = 0.70f))
            .border(1.dp, accent.copy(alpha = if (entry.unlocked) 0.50f else 0.22f), shape)
            .semantics { stateDescription = if (entry.unlocked) unlockedState else lockedState }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(entry.spriteRes),
                contentDescription = if (entry.unlocked) stringResource(entry.nameRes) else null,
                contentScale = ContentScale.Fit,
                colorFilter = if (entry.unlocked) {
                    ColorFilter.tint(Color(entry.tint), BlendMode.Modulate)
                } else {
                    ColorFilter.tint(IceMuted, BlendMode.SrcIn)
                },
                modifier = Modifier
                    .size(84.dp)
                    .graphicsLayer { alpha = if (entry.unlocked) 1f else 0.55f }
            )
        }

        Text(
            text = if (entry.unlocked) {
                stringResource(entry.nameRes)
            } else {
                stringResource(R.string.collection_locked_name)
            },
            style = MaterialTheme.typography.titleSmall,
            color = if (entry.unlocked) AccentDeep else TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        RarityTag(rarity = entry.rarity)
        Spacer(modifier = Modifier.height(4.dp))

        if (entry.unlocked) {
            if (entry.heaviestG > 0) {
                Text(
                    text = stringResource(
                        R.string.collection_heaviest,
                        formatNumber(entry.heaviestG)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            if (entry.count > 0) {
                Text(
                    text = stringResource(R.string.collection_count, entry.count),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = stringResource(R.string.collection_lane_hint, laneLabel),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun completion(found: Int, total: Int): Float {
    if (total <= 0) return 0f
    return found.toFloat() / total.toFloat()
}

