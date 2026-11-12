package com.Moxgume8.Wiclux.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Moxgume8.Wiclux.core.config.GameConfig
import com.Moxgume8.Wiclux.core.navigation.Screen
import com.Moxgume8.Wiclux.core.theme.SplashTop
import com.Moxgume8.Wiclux.presentation.collection.CollectionRoute
import com.Moxgume8.Wiclux.presentation.game.GameRoute
import com.Moxgume8.Wiclux.presentation.levels.LevelSelectRoute
import com.Moxgume8.Wiclux.presentation.menu.MenuRoute
import com.Moxgume8.Wiclux.presentation.progress.ProgressRoute
import com.Moxgume8.Wiclux.presentation.result.ResultRoute
import com.Moxgume8.Wiclux.presentation.settings.SettingsRoute
import com.Moxgume8.Wiclux.presentation.splash.SplashRoute

@Composable
fun AppRoot() {
    val factory = rememberAppFactory()
    val appViewModel: AppViewModel = viewModel(factory = factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()

    BackHandler(enabled = screen != Screen.Menu && screen != Screen.Splash) {
        appViewModel.back()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = SplashTop) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                (
                    fadeIn(animationSpec = tween(GameConfig.SCREEN_FADE_MS)) +
                        scaleIn(initialScale = 0.98f, animationSpec = tween(GameConfig.SCREEN_FADE_MS))
                    ) togetherWith fadeOut(animationSpec = tween(220))
            },
            label = "screen"
        ) { current ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (current) {
                    Screen.Splash -> SplashRoute(onFinished = { appViewModel.openMenu() })

                    Screen.Menu -> MenuRoute(
                        onPlay = { levelId -> appViewModel.navigate(Screen.Game(levelId)) },
                        onLevelSelect = { appViewModel.navigate(Screen.LevelSelect) },
                        onCollection = { appViewModel.navigate(Screen.Collection) },
                        onProgress = { appViewModel.navigate(Screen.Progress) },
                        onSettings = { appViewModel.navigate(Screen.Settings) }
                    )

                    Screen.LevelSelect -> LevelSelectRoute(
                        onStart = { levelId -> appViewModel.navigate(Screen.Game(levelId)) },
                        onMenu = { appViewModel.openMenu() }
                    )

                    is Screen.Game -> GameRoute(
                        levelId = current.levelId,
                        onFinished = { trip -> appViewModel.replace(Screen.Result(trip)) },
                        onMenu = { appViewModel.openMenu() }
                    )

                    is Screen.Result -> ResultRoute(
                        trip = current.trip,
                        onPlayAgain = { appViewModel.replace(Screen.Game(current.trip.levelId)) },
                        onLevelSelect = { appViewModel.replace(Screen.LevelSelect) },
                        onCollection = { appViewModel.navigate(Screen.Collection) },
                        onMenu = { appViewModel.openMenu() }
                    )

                    Screen.Collection -> CollectionRoute(onMenu = { appViewModel.openMenu() })

                    Screen.Progress -> ProgressRoute(onMenu = { appViewModel.openMenu() })

                    Screen.Settings -> SettingsRoute(onMenu = { appViewModel.openMenu() })
                }
            }
        }
    }
}
