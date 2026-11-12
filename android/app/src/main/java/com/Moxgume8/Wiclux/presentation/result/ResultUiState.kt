package com.Moxgume8.Wiclux.presentation.result

import com.Moxgume8.Wiclux.domain.model.Rarity

data class ResultCatchRow(
    val key: Int,
    val nameRes: Int,
    val spriteRes: Int,
    val tint: Long,
    val rarity: Rarity,
    val grams: Int,
    val isNew: Boolean
)

data class ResultUiState(
    val win: Boolean = false,
    val levelId: Int = 1,
    val levelNameRes: Int = 0,
    val grams: Int = 0,
    val targetGrams: Int = 0,
    val points: Int = 0,
    val stars: Int = 0,
    val bestStreak: Int = 0,
    val catchCount: Int = 0,
    val unlockedNext: Boolean = false,
    val rows: List<ResultCatchRow> = emptyList()
)
