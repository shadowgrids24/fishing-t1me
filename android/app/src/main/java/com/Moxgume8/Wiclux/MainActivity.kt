package com.Moxgume8.Wiclux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.Moxgume8.Wiclux.core.di.ServiceLocator
import com.Moxgume8.Wiclux.core.theme.AppTheme
import com.Moxgume8.Wiclux.presentation.app.AppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ServiceLocator.container(applicationContext)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}
