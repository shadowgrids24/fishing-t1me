package com.Moxgume8.Wiclux.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.Moxgume8.Wiclux.core.theme.AccentDeep
import com.Moxgume8.Wiclux.core.theme.IceSurface
import com.Moxgume8.Wiclux.core.theme.TextSecondary
import com.Moxgume8.Wiclux.core.ui.GlassPanel
import com.Moxgume8.Wiclux.core.ui.PrimaryButton
import com.Moxgume8.Wiclux.core.ui.SecondaryButton
import com.Moxgume8.Wiclux.core.ui.StatCardSpec
import com.Moxgume8.Wiclux.core.ui.StatRow
import com.Moxgume8.Wiclux.core.ui.formatNumber
import com.Moxgume8.Wiclux.presentation.app.rememberAppFactory

@Composable
fun MenuRoute(
    onPlay: (Int) -> Unit,
    onLevelSelect: () -> Unit,
    onCollection: () -> Unit,
    onProgress: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = rememberAppFactory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    MenuScreen(
        state = state,
        onPlay = { onPlay(state.playLevelId) },
        onLevelSelect = onLevelSelect,
        onCollection = onCollection,
        onProgress = onProgress,
        onSettings = onSettings
    )
}

@Composable
private fun MenuScreen(
    state: MenuUiState,
    onPlay: () -> Unit,
    onLevelSelect: () -> Unit,
    onCollection: () -> Unit,
    onProgress: () -> Unit,
    onSettings: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "menu")
    val breathe by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    Box(modifier = Modifier.fillMaxSize().background(IceSurface)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.60f)
        ) {
            Image(
                painter = painterResource(R.drawable.bg_game),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                0.55f to Color(0xFF0A4A6B).copy(alpha = 0.55f),
                                1f to IceSurface
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.cd_logo),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(132.dp)
                    .graphicsLayer {
                        scaleX = breathe
                        scaleY = breathe
                    }
            )
            Spacer(modifier = Modifier.weight(1f))

            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(28.dp),
                fill = Color.White.copy(alpha = 0.88f),
                borderColor = Color.White.copy(alpha = 0.70f),
                shadowColor = AccentDeep.copy(alpha = 0.22f),
                elevation = 22.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.title_app),
                        style = MaterialTheme.typography.headlineMedium,
                        color = AccentDeep,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.subtitle_app),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val cards = mutableListOf<StatCardSpec>()
                    if (state.bestWeightG > 0) {
                        cards.add(
                            StatCardSpec(
                                label = stringResource(R.string.stat_best_weight),
                                value = stringResource(
                                    R.string.grams_value,
                                    formatNumber(state.bestWeightG)
                                ),
                                accent = AccentDeep,
                                iconRes = R.drawable.ic_net
                            )
                        )
                    }
                    if (state.speciesFound > 0) {
                        cards.add(
                            StatCardSpec(
                                label = stringResource(R.string.stat_species),
                                value = stringResource(
                                    R.string.species_ratio,
                                    state.speciesFound,
                                    state.totalSpecies
                                ),
                                accent = AccentCyan,
                                iconRes = R.drawable.ic_star
                            )
                        )
                    }
                    StatRow(cards = cards)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.menu_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PrimaryButton(
                        text = stringResource(R.string.play),
                        onClick = onPlay,
                        modifier = Modifier.fillMaxWidth(),
                        iconRes = R.drawable.ic_hook
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SecondaryButton(
                            text = stringResource(R.string.level_select),
                            onClick = onLevelSelect,
                            modifier = Modifier.weight(1f),
                            iconRes = R.drawable.ic_drop_target
                        )
                        SecondaryButton(
                            text = stringResource(R.string.collection),
                            onClick = onCollection,
                            modifier = Modifier.weight(1f),
                            iconRes = R.drawable.ic_net
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SecondaryButton(
                            text = stringResource(R.string.progress),
                            onClick = onProgress,
                            modifier = Modifier.weight(1f),
                            height = 48.dp,
                            fill = Color.Transparent,
                            borderColor = AccentDeep.copy(alpha = 0.35f)
                        )
                        SecondaryButton(
                            text = stringResource(R.string.settings),
                            onClick = onSettings,
                            modifier = Modifier.weight(1f),
                            height = 48.dp,
                            fill = Color.Transparent,
                            borderColor = AccentDeep.copy(alpha = 0.35f),
                            iconRes = R.drawable.ic_gear
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
