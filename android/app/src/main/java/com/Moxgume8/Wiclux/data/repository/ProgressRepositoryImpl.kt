package com.Moxgume8.Wiclux.data.repository

import com.Moxgume8.Wiclux.data.local.PreferencesStore
import com.Moxgume8.Wiclux.domain.model.LevelRecord
import com.Moxgume8.Wiclux.domain.model.LevelSpec
import com.Moxgume8.Wiclux.domain.model.PlayerProgress
import com.Moxgume8.Wiclux.domain.model.Rarity
import com.Moxgume8.Wiclux.domain.model.Species
import com.Moxgume8.Wiclux.domain.model.SpeciesRecord
import com.Moxgume8.Wiclux.domain.model.TripResult
import com.Moxgume8.Wiclux.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.math.min

class ProgressRepositoryImpl(
    private val store: PreferencesStore,
    private val catalogue: List<Species>,
    private val levels: List<LevelSpec>
) : ProgressRepository {

    private val state = MutableStateFlow(load())

    override val progress: StateFlow<PlayerProgress> = state.asStateFlow()

    override fun recordTrip(result: TripResult) {
        val unlocked = store.readInt(KEY_UNLOCKED, 1)
        val previousStars = store.readInt(keyStars(result.levelId), 0)
        val previousBest = store.readInt(keyBestWeight(result.levelId), 0)
        store.writeInt(keyStars(result.levelId), max(previousStars, result.stars))
        store.writeInt(keyBestWeight(result.levelId), max(previousBest, result.grams))
        if (result.unlockedNext) {
            store.writeInt(KEY_UNLOCKED, min(levels.size, max(unlocked, result.levelId + 1)))
        }
        store.writeInt(KEY_LAST_LEVEL, result.levelId)

        val caught = store.readSet(KEY_SPECIES).toMutableSet()
        var rare = store.readInt(KEY_RARE, 0)
        for (record in result.catches) {
            caught.add(record.speciesId)
            val heaviestKey = keyHeaviest(record.speciesId)
            val countKey = keyCount(record.speciesId)
            store.writeInt(heaviestKey, max(store.readInt(heaviestKey, 0), record.grams))
            store.writeInt(countKey, store.readInt(countKey, 0) + 1)
            val species = catalogue.firstOrNull { it.id == record.speciesId }
            if (species != null && species.rarity == Rarity.RARE) rare++
        }
        store.writeSet(KEY_SPECIES, caught)
        store.writeInt(KEY_RARE, rare)
        store.writeInt(KEY_LIFETIME, store.readInt(KEY_LIFETIME, 0) + result.grams)
        store.writeInt(KEY_TRIPS, store.readInt(KEY_TRIPS, 0) + 1)
        store.writeInt(KEY_BEST_TRIP, max(store.readInt(KEY_BEST_TRIP, 0), result.grams))
        store.writeInt(KEY_BEST_STREAK, max(store.readInt(KEY_BEST_STREAK, 0), result.bestStreak))
        state.value = load()
    }

    override fun rememberLevel(levelId: Int) {
        store.writeInt(KEY_LAST_LEVEL, levelId)
        state.value = load()
    }

    override fun resetAll() {
        store.clearAll()
        state.value = load()
    }

    private fun load(): PlayerProgress {
        val levelRecords = levels.map { spec ->
            LevelRecord(
                levelId = spec.id,
                stars = store.readInt(keyStars(spec.id), 0),
                bestWeightG = store.readInt(keyBestWeight(spec.id), 0)
            )
        }
        val caught = store.readSet(KEY_SPECIES)
        val speciesRecords = catalogue
            .filter { caught.contains(it.id) }
            .map { species ->
                SpeciesRecord(
                    speciesId = species.id,
                    heaviestG = store.readInt(keyHeaviest(species.id), 0),
                    count = store.readInt(keyCount(species.id), 0)
                )
            }
        return PlayerProgress(
            unlockedSpots = store.readInt(KEY_UNLOCKED, 1).coerceIn(1, levels.size),
            lastLevelId = store.readInt(KEY_LAST_LEVEL, 1).coerceIn(1, levels.size),
            levels = levelRecords,
            species = speciesRecords,
            lifetimeWeightG = store.readInt(KEY_LIFETIME, 0),
            trips = store.readInt(KEY_TRIPS, 0),
            bestTripWeightG = store.readInt(KEY_BEST_TRIP, 0),
            bestStreak = store.readInt(KEY_BEST_STREAK, 0),
            rareCatches = store.readInt(KEY_RARE, 0)
        )
    }

    private fun keyStars(levelId: Int) = "stars_$levelId"

    private fun keyBestWeight(levelId: Int) = "best_weight_$levelId"

    private fun keyHeaviest(speciesId: String) = "species_heaviest_$speciesId"

    private fun keyCount(speciesId: String) = "species_count_$speciesId"

    private companion object {
        const val KEY_UNLOCKED = "unlocked_spots"
        const val KEY_SPECIES = "species_caught"
        const val KEY_LIFETIME = "lifetime_weight"
        const val KEY_TRIPS = "trips"
        const val KEY_BEST_TRIP = "best_trip_weight"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_RARE = "rare_catches"
        const val KEY_LAST_LEVEL = "last_level"
    }
}
