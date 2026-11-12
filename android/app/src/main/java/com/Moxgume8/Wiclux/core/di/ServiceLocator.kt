package com.Moxgume8.Wiclux.core.di

import android.content.Context

object ServiceLocator {

    private var instance: AppContainer? = null

    fun container(context: Context): AppContainer {
        val existing = instance
        if (existing != null) return existing
        val created = AppContainer(context.applicationContext)
        instance = created
        return created
    }
}
