package com.Moxgume8.Wiclux.domain.model

data class CatchRecord(
    val speciesId: String,
    val grams: Int,
    val points: Int,
    val multiplier: Float,
    val rippleHit: Boolean,
    val golden: Boolean,
    val firstEver: Boolean
)
