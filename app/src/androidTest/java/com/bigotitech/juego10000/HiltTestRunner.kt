package com.bigotitech.juego10000

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Runner personalizado para pruebas instrumentadas con Hilt.
 *
 * Este runner reemplaza la aplicación estándar por HiltTestApplication
 * para permitir la inyección de dependencias en las pruebas instrumentadas.
 */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
