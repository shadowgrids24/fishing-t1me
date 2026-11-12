package com.Moxgume8.Wiclux.domain.model

data class TripResult(
    val levelId: Int,
    val win: Boolean,
    val grams: Int,
    val targetGrams: Int,
    val points: Int,
    val stars: Int,
    val bestStreak: Int,
    val unlockedNext: Boolean,
    val catches: List<CatchRecord>
)
