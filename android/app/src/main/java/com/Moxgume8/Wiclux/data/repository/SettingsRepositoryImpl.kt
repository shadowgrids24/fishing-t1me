package com.Moxgume8.Wiclux.data.repository

import com.Moxgume8.Wiclux.data.local.PreferencesStore
import com.Moxgume8.Wiclux.domain.model.GameSettings
import com.Moxgume8.Wiclux.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(private val store: PreferencesStore) : SettingsRepository {

    private val state = MutableStateFlow(load())

    override val settings: StateFlow<GameSettings> = state.asStateFlow()

    override fun setAimAssist(enabled: Boolean) {
        store.writeBoolean(KEY_AIM_ASSIST, enabled)
        state.value = load()
    }

    override fun setSwiftHook(enabled: Boolean) {
        store.writeBoolean(KEY_HOOK_SWIFT, enabled)
        state.value = load()
    }

    override fun setRippleMarkers(enabled: Boolean) {
        store.writeBoolean(KEY_RIPPLE_MARKERS, enabled)
        state.value = load()
    }

    override fun setHighContrast(enabled: Boolean) {
        store.writeBoolean(KEY_HIGH_CONTRAST, enabled)
        state.value = load()
    }

    fun reload() {
        state.value = load()
    }

    private fun load(): GameSettings = GameSettings(
        aimAssist = store.readBoolean(KEY_AIM_ASSIST, true),
        swiftHook = store.readBoolean(KEY_HOOK_SWIFT, false),
        rippleMarkers = store.readBoolean(KEY_RIPPLE_MARKERS, true),
        highContrast = store.readBoolean(KEY_HIGH_CONTRAST, false)
    )

    private companion object {
        const val KEY_AIM_ASSIST = "set_aim_assist"
        const val KEY_HOOK_SWIFT = "set_hook_swift"
        const val KEY_RIPPLE_MARKERS = "set_ripple_markers"
        const val KEY_HIGH_CONTRAST = "set_high_contrast"
    }
}
