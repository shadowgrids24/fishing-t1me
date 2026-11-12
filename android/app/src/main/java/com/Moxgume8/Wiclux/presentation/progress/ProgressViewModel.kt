package com.Moxgume8.Wiclux.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Moxgume8.Wiclux.domain.model.LevelSpec
import com.Moxgume8.Wiclux.domain.model.Rarity
import com.Moxgume8.Wiclux.domain.model.Species
import com.Moxgume8.Wiclux.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val progressRepository: ProgressRepository,
    private val catalogue: List<Species>,
    private val levels: List<LevelSpec>
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressUiState(totalSpecies = catalogue.size))
    val state: StateFlow<ProgressUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            progressRepository.progress.collect { progress ->
                val spots = levels.map { spec ->
                    val record = progress.recordFor(spec.id)
                    SpotProgressRow(
                        levelId = spec.id,
                        nameRes = spec.nameRes,
                        stars = record?.stars ?: 0,
                        bestWeightG = record?.bestWeightG ?: 0,
                        targetGrams = spec.targetGrams,
                        unlocked = spec.id <= progress.unlockedSpots
                    )
                }
                var common = 0
                var uncommon = 0
                var rare = 0
                for (record in progress.species) {
                    val species = catalogue.firstOrNull { it.id == record.speciesId } ?: continue
                    when (species.rarity) {
                        Rarity.COMMON -> common += record.count
                        Rarity.UNCOMMON -> uncommon += record.count
                        Rarity.RARE -> rare += record.count
                    }
                }
                _state.value = ProgressUiState(
                    lifetimeWeightG = progress.lifetimeWeightG,
                    trips = progress.trips,
                    bestTripWeightG = progress.bestTripWeightG,
                    speciesFound = progress.speciesFound,
                    totalSpecies = catalogue.size,
                    bestStreak = progress.bestStreak,
                    rareCatches = progress.rareCatches,
                    spots = spots,
                    commonCount = common,
                    uncommonCount = uncommon,
                    rareCount = rare
                )
            }
        }
    }
}
