package com.Moxgume8.Wiclux.core.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Moxgume8.Wiclux.R
import com.Moxgume8.Wiclux.core.theme.IceBorder
import com.Moxgume8.Wiclux.core.theme.RarityCommon
import com.Moxgume8.Wiclux.core.theme.RarityGold
import com.Moxgume8.Wiclux.core.theme.RarityRare
import com.Moxgume8.Wiclux.core.theme.RarityUncommon
import com.Moxgume8.Wiclux.domain.model.Rarity

@Composable
fun rarityColor(rarity: Rarity): Color = when (rarity) {
    Rarity.COMMON -> RarityCommon
    Rarity.UNCOMMON -> RarityUncommon
    Rarity.RARE -> RarityRare
}

@Composable
fun rarityLabel(rarity: Rarity): String = when (rarity) {
    Rarity.COMMON -> stringResource(R.string.rarity_common)
    Rarity.UNCOMMON -> stringResource(R.string.rarity_uncommon)
    Rarity.RARE -> stringResource(R.string.rarity_rare)
}

@Composable
fun RarityTag(rarity: Rarity, modifier: Modifier = Modifier) {
    val color = rarityColor(rarity)
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(color.copy(alpha = 0.16f))
            .border(1.dp, color.copy(alpha = 0.46f), shape)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = rarityLabel(rarity),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun StarStrip(
    earned: Int,
    modifier: Modifier = Modifier,
    total: Int = 3,
    animateIndex: Int? = null,
    starSize: Dp = 18.dp,
    emptyColor: Color = IceBorder
) {
    Row(
        modifier = modifier.semantics {
            stateDescription = starsDescription(earned, total)
        },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until total) {
            val filled = index < earned
            val highlight = animateIndex != null && animateIndex == index
            val scale by animateFloatAsState(
                targetValue = if (highlight) 1.15f else 1f,
                animationSpec = spring(dampingRatio = 0.45f, stiffness = 320f),
                label = "star"
            )
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                tint = if (filled) RarityGold else emptyColor,
                modifier = Modifier
                    .size(starSize)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }
    }
}

@Composable
fun EmptyStateMessage(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = RarityCommon
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 26.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

private fun starsDescription(earned: Int, total: Int): String = "$earned / $total"
