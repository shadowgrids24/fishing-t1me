package com.Moxgume8.Wiclux.presentation.collection

import com.Moxgume8.Wiclux.domain.model.Lane
import com.Moxgume8.Wiclux.domain.model.Rarity

data class CollectionEntry(
    val speciesId: String,
    val nameRes: Int,
    val spriteRes: Int,
    val tint: Long,
    val rarity: Rarity,
    val lane: Lane,
    val unlocked: Boolean,
    val heaviestG: Int,
    val count: Int
)

data class CollectionUiState(
    val entries: List<CollectionEntry> = emptyList(),
    val found: Int = 0,
    val total: Int = 0
)
