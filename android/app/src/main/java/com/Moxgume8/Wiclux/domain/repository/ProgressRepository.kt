package com.Moxgume8.Wiclux.domain.repository

import com.Moxgume8.Wiclux.domain.model.PlayerProgress
import com.Moxgume8.Wiclux.domain.model.TripResult
import kotlinx.coroutines.flow.StateFlow

interface ProgressRepository {
    val progress: StateFlow<PlayerProgress>
    fun recordTrip(result: TripResult)
    fun rememberLevel(levelId: Int)
    fun resetAll()
}
