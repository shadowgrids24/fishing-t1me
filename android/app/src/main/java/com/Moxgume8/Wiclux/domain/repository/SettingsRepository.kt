package com.Moxgume8.Wiclux.domain.repository

import com.Moxgume8.Wiclux.domain.model.GameSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<GameSettings>
    fun setAimAssist(enabled: Boolean)
    fun setSwiftHook(enabled: Boolean)
    fun setRippleMarkers(enabled: Boolean)
    fun setHighContrast(enabled: Boolean)
}
