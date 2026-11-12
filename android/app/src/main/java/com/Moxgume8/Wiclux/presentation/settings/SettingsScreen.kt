package com.Moxgume8.Wiclux.presentation.settings

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.Moxgume8.Wiclux.core.theme.IceCard
import com.Moxgume8.Wiclux.core.theme.TextOnWater
import com.Moxgume8.Wiclux.core.theme.TextSecondary
import com.Moxgume8.Wiclux.core.theme.WarnAmber
import com.Moxgume8.Wiclux.core.ui.AuroraBackground
import com.Moxgume8.Wiclux.core.ui.ScreenHeader
import com.Moxgume8.Wiclux.core.ui.SecondaryButton
import com.Moxgume8.Wiclux.presentation.app.rememberAppFactory
import com.Moxgume8.Wiclux.presentation.dialog.ResetProgressDialog

@Composable
fun SettingsRoute(onMenu: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = rememberAppFactory())
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAimAssist = { enabled -> settingsViewModel.setAimAssist(enabled) },
        onSwiftHook = { enabled -> settingsViewModel.setSwiftHook(enabled) },
        onRippleMarkers = { enabled -> settingsViewModel.setRippleMarkers(enabled) },
        onHighContrast = { enabled -> settingsViewModel.setHighContrast(enabled) },
        onResetRequest = { settingsViewModel.showResetDialog() },
        onMenu = onMenu
    )

    if (state.resetDialogVisible) {
        ResetProgressDialog(
            onConfirm = { settingsViewModel.confirmReset() },
            onCancel = { settingsViewModel.hideResetDialog() }
        )
    }
}

@Composable
private fun SettingsScreen(
    state: SettingsUiState,
    onAimAssist: (Boolean) -> Unit,
    onSwiftHook: (Boolean) -> Unit,
    onRippleMarkers: (Boolean) -> Unit,
    onHighContrast: (Boolean) -> Unit,
    onResetRequest: () -> Unit,
    onMenu: () -> Unit
) {
    AuroraBackground(
        scrim = listOf(
            Color(0xFFE8F4F8).copy(alpha = 0.94f),
            Color(0xFFD1E7EE).copy(alpha = 0.96f)
        ),
        artAlpha = 0.28f,
        firstBlobAlpha = 0.14f,
        secondBlobAlpha = 0.10f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.settings_title),
                onMenu = onMenu
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 26.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SettingOptionRow(
                        title = stringResource(R.string.setting_aim_assist),
                        description = stringResource(R.string.setting_aim_assist_desc),
                        firstLabel = stringResource(R.string.option_guide_line),
                        secondLabel = stringResource(R.string.option_off),
                        firstSelected = state.aimAssist,
                        onFirst = { onAimAssist(true) },
                        onSecond = { onAimAssist(false) }
                    )
                }
                item {
                    SettingOptionRow(
                        title = stringResource(R.string.setting_hook_speed),
                        description = stringResource(R.string.setting_hook_speed_desc),
                        firstLabel = stringResource(R.string.option_calm),
                        secondLabel = stringResource(R.string.option_swift),
                        firstSelected = !state.swiftHook,
                        onFirst = { onSwiftHook(false) },
                        onSecond = { onSwiftHook(true) }
                    )
                }
                item {
                    SettingOptionRow(
                        title = stringResource(R.string.setting_ripple_markers),
                        description = stringResource(R.string.setting_ripple_markers_desc),
                        firstLabel = stringResource(R.string.option_on),
                        secondLabel = stringResource(R.string.option_off),
                        firstSelected = state.rippleMarkers,
                        onFirst = { onRippleMarkers(true) },
                        onSecond = { onRippleMarkers(false) }
                    )
                }
                item {
                    SettingOptionRow(
                        title = stringResource(R.string.setting_high_contrast),
                        description = stringResource(R.string.setting_high_contrast_desc),
                        firstLabel = stringResource(R.string.option_on),
                        secondLabel = stringResource(R.string.option_off),
                        firstSelected = state.highContrast,
                        onFirst = { onHighContrast(true) },
                        onSecond = { onHighContrast(false) }
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.78f))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.setting_reset_desc),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        SecondaryButton(
                            text = stringResource(R.string.setting_reset),
                            onClick = onResetRequest,
                            modifier = Modifier.fillMaxWidth(),
                            fill = WarnAmber.copy(alpha = 0.14f),
                            borderColor = WarnAmber,
                            contentColor = WarnAmber
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingOptionRow(
    title: String,
    description: String,
    firstLabel: String,
    secondLabel: String,
    firstSelected: Boolean,
    onFirst: () -> Unit,
    onSecond: () -> Unit
) {
    val current = if (firstSelected) firstLabel else secondLabel
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.78f))
            .semantics { stateDescription = current }
            .padding(14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = AccentDeep
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OptionChip(
                label = firstLabel,
                selected = firstSelected,
                onClick = onFirst,
                modifier = Modifier.weight(1f)
            )
            OptionChip(
                label = secondLabel,
                selected = !firstSelected,
                onClick = onSecond,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .background(if (selected) AccentCyan else IceCard.copy(alpha = 0.6f))
            .border(1.dp, if (selected) AccentCyan else TextSecondary.copy(alpha = 0.26f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) TextOnWater else TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
