package com.Moxgume8.Wiclux.domain.usecase

import com.Moxgume8.Wiclux.domain.model.CatchRecord
import com.Moxgume8.Wiclux.domain.model.LevelSpec
import com.Moxgume8.Wiclux.domain.model.TripResult

class ResolveTripUseCase {

    operator fun invoke(
        level: LevelSpec,
        catches: List<CatchRecord>,
        bestStreak: Int,
        unlockedSpots: Int,
        totalLevels: Int
    ): TripResult {
        val grams = catches.sumOf { it.grams }
        val points = catches.sumOf { it.points }
        val win = grams >= level.targetGrams
        val stars = when {
            grams >= level.targetGrams * 1.8f -> 3
            grams >= level.targetGrams * 1.4f -> 2
            win -> 1
            else -> 0
        }
        val unlockedNext = win && level.id >= unlockedSpots && level.id < totalLevels
        return TripResult(
            levelId = level.id,
            win = win,
            grams = grams,
            targetGrams = level.targetGrams,
            points = points,
            stars = stars,
            bestStreak = bestStreak,
            unlockedNext = unlockedNext,
            catches = catches
        )
    }
}
