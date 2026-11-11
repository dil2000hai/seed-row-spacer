package com.seedrow.spacer.core.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.seedrow.spacer.data.local.GardenPreferences
import com.seedrow.spacer.data.local.SettingsPreferences
import com.seedrow.spacer.data.repository.GardenRepositoryImpl
import com.seedrow.spacer.data.repository.SettingsRepositoryImpl

object ServiceLocator {

    private var factory: ViewModelProvider.Factory? = null

    fun init(context: Context) {
        if (factory != null) return
        val appContext = context.applicationContext
        val garden = GardenRepositoryImpl(GardenPreferences(appContext))
        val settings = SettingsRepositoryImpl(SettingsPreferences(appContext))
        factory = AppViewModelFactory.create(garden, settings)
    }

    val viewModelFactory: ViewModelProvider.Factory
        get() = checkNotNull(factory)
}
