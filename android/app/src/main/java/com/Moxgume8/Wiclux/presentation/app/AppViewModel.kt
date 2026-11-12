package com.Moxgume8.Wiclux.presentation.app

import androidx.lifecycle.ViewModel
import com.Moxgume8.Wiclux.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val history = mutableListOf<Screen>()
    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    fun openMenu() {
        history.clear()
        _screen.value = Screen.Menu
    }

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) history.add(current)
        _screen.value = target
    }

    fun replace(target: Screen) {
        _screen.value = target
    }

    fun back() {
        if (history.isEmpty()) return
        val index = history.size - 1
        val previous = history[index]
        history.removeAt(index)
        _screen.value = previous
    }

    fun canGoBack(): Boolean = history.isNotEmpty()
}
