package com.nutrizulia

import android.app.Application
import com.nutrizulia.util.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NutriZuliaApp: Application() {

    @Inject
    lateinit var syncScheduler: SyncScheduler

    override fun onCreate() {
        super.onCreate()
        
        // Inicializar la sincronización periódica
        // Esto programará la sincronización cada 15 minutos
        syncScheduler.schedulePeriodic()
    }
}