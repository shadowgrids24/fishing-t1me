package com.Moxgume8.Wiclux.presentation.progress

data class SpotProgressRow(
    val levelId: Int,
    val nameRes: Int,
    val stars: Int,
    val bestWeightG: Int,
    val targetGrams: Int,
    val unlocked: Boolean
)

data class ProgressUiState(
    val lifetimeWeightG: Int = 0,
    val trips: Int = 0,
    val bestTripWeightG: Int = 0,
    val speciesFound: Int = 0,
    val totalSpecies: Int = 0,
    val bestStreak: Int = 0,
    val rareCatches: Int = 0,
    val spots: List<SpotProgressRow> = emptyList(),
    val commonCount: Int = 0,
    val uncommonCount: Int = 0,
    val rareCount: Int = 0
) {
    val totalCatches: Int get() = commonCount + uncommonCount + rareCount
}
