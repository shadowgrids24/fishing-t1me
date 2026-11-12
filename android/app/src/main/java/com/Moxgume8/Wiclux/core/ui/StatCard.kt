package com.Moxgume8.Wiclux.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Moxgume8.Wiclux.core.theme.TextSecondary

data class StatCardSpec(
    val label: String,
    val value: String,
    val accent: Color,
    val iconRes: Int? = null
)

@Composable
fun StatCard(
    spec: StatCardSpec,
    modifier: Modifier = Modifier,
    onDark: Boolean = false
) {
    if (!hasValue(spec)) return
    val shape = RoundedCornerShape(18.dp)
    val surface = if (onDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.78f)
    val labelColor = if (onDark) Color.White.copy(alpha = 0.62f) else TextSecondary
    Column(
        modifier = modifier
            .heightIn(min = 72.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(surface, spec.accent.copy(alpha = if (onDark) 0.14f else 0.10f))
                )
            )
            .border(1.dp, spec.accent.copy(alpha = 0.42f), shape)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (spec.iconRes != null) {
                Icon(
                    painter = painterResource(spec.iconRes),
                    contentDescription = null,
                    tint = spec.accent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(spec.accent)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = spec.value,
                style = MaterialTheme.typography.headlineSmall,
                color = spec.accent,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = spec.label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StatRow(
    cards: List<StatCardSpec>,
    modifier: Modifier = Modifier,
    onDark: Boolean = false
) {
    val visible = cards.filter { hasValue(it) }
    if (visible.size < 2) return
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (spec in visible) {
            StatCard(spec = spec, modifier = Modifier.weight(1f), onDark = onDark)
        }
    }
}

private fun hasValue(spec: StatCardSpec): Boolean {
    val trimmed = spec.value.trim()
    return trimmed.isNotEmpty() && trimmed != "0"
}
