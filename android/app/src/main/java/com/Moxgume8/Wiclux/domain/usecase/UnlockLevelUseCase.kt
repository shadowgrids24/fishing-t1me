package com.Moxgume8.Wiclux.domain.usecase

import com.Moxgume8.Wiclux.domain.model.TripResult
import com.Moxgume8.Wiclux.domain.repository.ProgressRepository

class UnlockLevelUseCase(private val repository: ProgressRepository) {

    operator fun invoke(result: TripResult) {
        repository.recordTrip(result)
    }
}
