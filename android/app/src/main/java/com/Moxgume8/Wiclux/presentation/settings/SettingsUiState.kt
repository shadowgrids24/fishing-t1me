package com.Moxgume8.Wiclux.presentation.settings

data class SettingsUiState(
    val aimAssist: Boolean = true,
    val swiftHook: Boolean = false,
    val rippleMarkers: Boolean = true,
    val highContrast: Boolean = false,
    val resetDialogVisible: Boolean = false
)
