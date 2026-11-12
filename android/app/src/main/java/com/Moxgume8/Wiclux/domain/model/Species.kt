package com.Moxgume8.Wiclux.domain.model

data class Species(
    val id: String,
    val nameRes: Int,
    val spriteRes: Int,
    val tint: Long,
    val lane: Lane,
    val rarity: Rarity,
    val weightG: Int,
    val basePoints: Int,
    val sizeDp: Float
)
