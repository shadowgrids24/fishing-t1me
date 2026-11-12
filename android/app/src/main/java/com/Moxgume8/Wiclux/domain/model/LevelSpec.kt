package com.Moxgume8.Wiclux.domain.model

data class LevelSpec(
    val id: Int,
    val nameRes: Int,
    val hintRes: Int,
    val depthM: Int,
    val targetGrams: Int,
    val speedScale: Float,
    val shardCount: Int,
    val lanes: List<Lane>
)
