package com.Moxgume8.Wiclux.presentation.result

import androidx.lifecycle.ViewModel
import com.Moxgume8.Wiclux.domain.model.LevelSpec
import com.Moxgume8.Wiclux.domain.model.Species
import com.Moxgume8.Wiclux.domain.model.TripResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResultViewModel(
    private val catalogue: List<Species>,
    private val levels: List<LevelSpec>
) : ViewModel() {

    private val _state = MutableStateFlow(ResultUiState())
    val state: StateFlow<ResultUiState> = _state.asStateFlow()

    fun bind(trip: TripResult) {
        val level = levels.firstOrNull { it.id == trip.levelId } ?: levels[0]
        var key = 0
        val rows = trip.catches.mapNotNull { record ->
            val species = catalogue.firstOrNull { it.id == record.speciesId }
            if (species == null) {
                null
            } else {
                key++
                ResultCatchRow(
                    key = key,
                    nameRes = species.nameRes,
                    spriteRes = species.spriteRes,
                    tint = species.tint,
                    rarity = species.rarity,
                    grams = record.grams,
                    isNew = record.firstEver
                )
            }
        }
        _state.value = ResultUiState(
            win = trip.win,
            levelId = trip.levelId,
            levelNameRes = level.nameRes,
            grams = trip.grams,
            targetGrams = trip.targetGrams,
            points = trip.points,
            stars = trip.stars,
            bestStreak = trip.bestStreak,
            catchCount = trip.catches.size,
            unlockedNext = trip.unlockedNext,
            rows = rows
        )
    }
}
