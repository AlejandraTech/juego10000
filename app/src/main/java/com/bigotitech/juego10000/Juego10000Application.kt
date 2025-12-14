package com.bigotitech.juego10000

import android.app.Application
import com.bigotitech.juego10000.ads.AdManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Clase principal de la aplicación que inicializa Hilt para la inyección de dependencias.
 *
 * Esta clase sirve como punto de entrada para la configuración de la aplicación y
 * está anotada con @HiltAndroidApp para habilitar la generación de componentes de Hilt.
 */
@HiltAndroidApp
class Juego10000Application : Application() {

    @Inject
    lateinit var adManager: AdManager

    override fun onCreate() {
        super.onCreate()
        adManager.initialize()
    }
}