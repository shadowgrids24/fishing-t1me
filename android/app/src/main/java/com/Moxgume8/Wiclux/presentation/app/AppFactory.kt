package com.Moxgume8.Wiclux.presentation.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.Moxgume8.Wiclux.core.di.AppViewModelFactory
import com.Moxgume8.Wiclux.core.di.ServiceLocator

@Composable
fun rememberAppFactory(): AppViewModelFactory {
    val context = LocalContext.current
    return remember(context) { AppViewModelFactory(ServiceLocator.container(context)) }
}
