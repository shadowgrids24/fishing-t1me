package com.Moxgume8.Wiclux.domain.usecase

import com.Moxgume8.Wiclux.domain.model.GameSettings
import com.Moxgume8.Wiclux.domain.model.Species
import kotlin.math.roundToInt

class ScoreCatchUseCase {

    fun multiplier(
        species: Species,
        rippleHit: Boolean,
        golden: Boolean,
        settings: GameSettings
    ): Float {
        val rippleMultiplier = when {
            golden -> 3.0f
            rippleHit && !settings.rippleMarkers -> 2.4f
            rippleHit -> 2.0f
            else -> 1.0f
        }
        val assistMultiplier = if (settings.aimAssist) 1.0f else 1.12f
        val speedMultiplier = if (settings.swiftHook) 1.15f else 1.0f
        return species.rarity.multiplier * rippleMultiplier * assistMultiplier * speedMultiplier
    }

    fun points(
        species: Species,
        rippleHit: Boolean,
        golden: Boolean,
        settings: GameSettings
    ): Int = (species.basePoints * multiplier(species, rippleHit, golden, settings)).roundToInt()
}
