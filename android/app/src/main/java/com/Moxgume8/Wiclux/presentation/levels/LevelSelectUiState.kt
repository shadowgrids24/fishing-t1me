package com.Moxgume8.Wiclux.presentation.levels

data class SpotRow(
    val id: Int,
    val nameRes: Int,
    val hintRes: Int,
    val depthM: Int,
    val targetGrams: Int,
    val stars: Int,
    val bestWeightG: Int,
    val unlocked: Boolean
)

data class LevelSelectUiState(
    val spots: List<SpotRow> = emptyList(),
    val unlocked: Int = 1,
    val total: Int = 0,
    val selectedId: Int = 0
)
