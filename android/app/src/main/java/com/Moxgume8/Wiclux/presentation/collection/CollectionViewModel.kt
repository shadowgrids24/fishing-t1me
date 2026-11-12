package com.Moxgume8.Wiclux.presentation.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Moxgume8.Wiclux.domain.model.Species
import com.Moxgume8.Wiclux.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionViewModel(
    private val progressRepository: ProgressRepository,
    private val catalogue: List<Species>
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionUiState(total = catalogue.size))
    val state: StateFlow<CollectionUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            progressRepository.progress.collect { progress ->
                val entries = catalogue.map { species ->
                    val record = progress.recordFor(species.id)
                    CollectionEntry(
                        speciesId = species.id,
                        nameRes = species.nameRes,
                        spriteRes = species.spriteRes,
                        tint = species.tint,
                        rarity = species.rarity,
                        lane = species.lane,
                        unlocked = record != null,
                        heaviestG = record?.heaviestG ?: 0,
                        count = record?.count ?: 0
                    )
                }
                _state.value = CollectionUiState(
                    entries = entries,
                    found = entries.count { it.unlocked },
                    total = catalogue.size
                )
            }
        }
    }
}
