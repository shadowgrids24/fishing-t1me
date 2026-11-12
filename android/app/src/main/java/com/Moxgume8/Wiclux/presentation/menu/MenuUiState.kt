package com.Moxgume8.Wiclux.presentation.menu

import com.Moxgume8.Wiclux.core.config.GameConfig

data class MenuUiState(
    val bestWeightG: Int = 0,
    val speciesFound: Int = 0,
    val totalSpecies: Int = GameConfig.TOTAL_SPECIES,
    val playLevelId: Int = 1
)
