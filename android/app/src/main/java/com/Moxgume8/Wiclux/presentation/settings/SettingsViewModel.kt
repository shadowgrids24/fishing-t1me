package com.Moxgume8.Wiclux.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Moxgume8.Wiclux.domain.repository.ProgressRepository
import com.Moxgume8.Wiclux.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update { current ->
                    current.copy(
                        aimAssist = settings.aimAssist,
                        swiftHook = settings.swiftHook,
                        rippleMarkers = settings.rippleMarkers,
                        highContrast = settings.highContrast
                    )
                }
            }
        }
    }

    fun setAimAssist(enabled: Boolean) {
        settingsRepository.setAimAssist(enabled)
    }

    fun setSwiftHook(enabled: Boolean) {
        settingsRepository.setSwiftHook(enabled)
    }

    fun setRippleMarkers(enabled: Boolean) {
        settingsRepository.setRippleMarkers(enabled)
    }

    fun setHighContrast(enabled: Boolean) {
        settingsRepository.setHighContrast(enabled)
    }

    fun showResetDialog() {
        _state.update { it.copy(resetDialogVisible = true) }
    }

    fun hideResetDialog() {
        _state.update { it.copy(resetDialogVisible = false) }
    }

    fun confirmReset() {
        progressRepository.resetAll()
        settingsRepository.setAimAssist(true)
        settingsRepository.setSwiftHook(false)
        settingsRepository.setRippleMarkers(true)
        settingsRepository.setHighContrast(false)
        _state.update { it.copy(resetDialogVisible = false) }
    }
}
