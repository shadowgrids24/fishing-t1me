package com.Moxgume8.Wiclux.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.Moxgume8.Wiclux.presentation.app.AppViewModel
import com.Moxgume8.Wiclux.presentation.collection.CollectionViewModel
import com.Moxgume8.Wiclux.presentation.game.GameViewModel
import com.Moxgume8.Wiclux.presentation.levels.LevelSelectViewModel
import com.Moxgume8.Wiclux.presentation.menu.MenuViewModel
import com.Moxgume8.Wiclux.presentation.progress.ProgressViewModel
import com.Moxgume8.Wiclux.presentation.result.ResultViewModel
import com.Moxgume8.Wiclux.presentation.settings.SettingsViewModel
import com.Moxgume8.Wiclux.presentation.splash.SplashViewModel

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel()

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(container.progressRepository)

            modelClass.isAssignableFrom(LevelSelectViewModel::class.java) ->
                LevelSelectViewModel(container.progressRepository, container.levels)

            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(
                    container.progressRepository,
                    container.settingsRepository,
                    container.scoreCatch,
                    container.resolveTrip,
                    container.unlockLevel,
                    container.species,
                    container.levels
                )

            modelClass.isAssignableFrom(ResultViewModel::class.java) ->
                ResultViewModel(container.species, container.levels)

            modelClass.isAssignableFrom(CollectionViewModel::class.java) ->
                CollectionViewModel(container.progressRepository, container.species)

            modelClass.isAssignableFrom(ProgressViewModel::class.java) ->
                ProgressViewModel(
                    container.progressRepository,
                    container.species,
                    container.levels
                )

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(container.settingsRepository, container.progressRepository)

            else -> AppViewModel()
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }
}
