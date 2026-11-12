package com.Moxgume8.Wiclux.domain.model

data class LevelRecord(
    val levelId: Int,
    val stars: Int,
    val bestWeightG: Int
)

data class SpeciesRecord(
    val speciesId: String,
    val heaviestG: Int,
    val count: Int
)

data class PlayerProgress(
    val unlockedSpots: Int,
    val lastLevelId: Int,
    val levels: List<LevelRecord>,
    val species: List<SpeciesRecord>,
    val lifetimeWeightG: Int,
    val trips: Int,
    val bestTripWeightG: Int,
    val bestStreak: Int,
    val rareCatches: Int
) {
    val speciesFound: Int get() = species.size

    fun recordFor(levelId: Int): LevelRecord? = levels.firstOrNull { it.levelId == levelId }

    fun recordFor(speciesId: String): SpeciesRecord? = species.firstOrNull { it.speciesId == speciesId }

    companion object {
        val EMPTY = PlayerProgress(
            unlockedSpots = 1,
            lastLevelId = 1,
            levels = emptyList(),
            species = emptyList(),
            lifetimeWeightG = 0,
            trips = 0,
            bestTripWeightG = 0,
            bestStreak = 0,
            rareCatches = 0
        )
    }
}
