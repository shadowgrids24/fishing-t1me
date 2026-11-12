package com.Moxgume8.Wiclux.core.di

import android.content.Context
import com.Moxgume8.Wiclux.data.local.PreferencesStore
import com.Moxgume8.Wiclux.data.repository.ProgressRepositoryImpl
import com.Moxgume8.Wiclux.data.repository.SettingsRepositoryImpl
import com.Moxgume8.Wiclux.data.sample.SampleData
import com.Moxgume8.Wiclux.domain.model.LevelSpec
import com.Moxgume8.Wiclux.domain.model.Species
import com.Moxgume8.Wiclux.domain.repository.ProgressRepository
import com.Moxgume8.Wiclux.domain.repository.SettingsRepository
import com.Moxgume8.Wiclux.domain.usecase.ResolveTripUseCase
import com.Moxgume8.Wiclux.domain.usecase.ScoreCatchUseCase
import com.Moxgume8.Wiclux.domain.usecase.UnlockLevelUseCase

class AppContainer(context: Context) {

    val species: List<Species> = SampleData.species
    val levels: List<LevelSpec> = SampleData.levels

    private val store = PreferencesStore(context)
    private val settingsImpl = SettingsRepositoryImpl(store)

    val progressRepository: ProgressRepository = ProgressRepositoryImpl(store, species, levels)
    val settingsRepository: SettingsRepository = settingsImpl

    val scoreCatch = ScoreCatchUseCase()
    val resolveTrip = ResolveTripUseCase()
    val unlockLevel = UnlockLevelUseCase(progressRepository)

    fun refreshSettings() {
        settingsImpl.reload()
    }
}
