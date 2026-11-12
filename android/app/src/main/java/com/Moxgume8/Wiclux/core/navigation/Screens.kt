package com.Moxgume8.Wiclux.core.navigation

import com.Moxgume8.Wiclux.domain.model.TripResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object LevelSelect : Screen
    data class Game(val levelId: Int) : Screen
    data class Result(val trip: TripResult) : Screen
    data object Collection : Screen
    data object Progress : Screen
    data object Settings : Screen
}
