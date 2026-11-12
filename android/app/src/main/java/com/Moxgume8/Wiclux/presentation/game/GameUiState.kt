package com.Moxgume8.Wiclux.presentation.game

import com.Moxgume8.Wiclux.core.config.GameConfig
import com.Moxgume8.Wiclux.domain.game.LakePhase
import com.Moxgume8.Wiclux.domain.game.LakeView
import com.Moxgume8.Wiclux.domain.model.GameSettings
import com.Moxgume8.Wiclux.domain.model.TripResult

data class CatchFlash(
    val id: Int,
    val grams: Int,
    val multiplier: Float,
    val x: Float,
    val y: Float,
    val golden: Boolean
)

data class GameUiState(
    val levelId: Int = 1,
    val levelNameRes: Int = 0,
    val depthM: Int = 0,
    val targetGrams: Int = 0,
    val grams: Int = 0,
    val points: Int = 0,
    val linesLeft: Int = GameConfig.LINES_PER_TRIP,
    val maxLines: Int = GameConfig.LINES_PER_TRIP,
    val streak: Int = 0,
    val phase: LakePhase = LakePhase.IDLE,
    val lake: LakeView? = null,
    val settings: GameSettings = GameSettings(),
    val paused: Boolean = false,
    val finished: Boolean = false,
    val flash: CatchFlash? = null,
    val shakeTick: Int = 0,
    val hintBright: Boolean = true
)

sealed interface GameEvent {
    data class Finished(val trip: TripResult) : GameEvent
}
