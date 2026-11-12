package com.Moxgume8.Wiclux.presentation.levels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Moxgume8.Wiclux.domain.model.LevelSpec
import com.Moxgume8.Wiclux.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LevelSelectViewModel(
    private val progressRepository: ProgressRepository,
    private val levels: List<LevelSpec>
) : ViewModel() {

    private val _state = MutableStateFlow(LevelSelectUiState(total = levels.size))
    val state: StateFlow<LevelSelectUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            progressRepository.progress.collect { progress ->
                val rows = levels.map { spec ->
                    val record = progress.recordFor(spec.id)
                    SpotRow(
                        id = spec.id,
                        nameRes = spec.nameRes,
                        hintRes = spec.hintRes,
                        depthM = spec.depthM,
                        targetGrams = spec.targetGrams,
                        stars = record?.stars ?: 0,
                        bestWeightG = record?.bestWeightG ?: 0,
                        unlocked = spec.id <= progress.unlockedSpots
                    )
                }
                _state.update { current ->
                    current.copy(
                        spots = rows,
                        unlocked = progress.unlockedSpots,
                        total = levels.size,
                        selectedId = if (current.selectedId == 0) progress.lastLevelId else current.selectedId
                    )
                }
            }
        }
    }

    fun select(levelId: Int) {
        _state.update { current ->
            current.copy(selectedId = if (current.selectedId == levelId) 0 else levelId)
        }
    }
}
