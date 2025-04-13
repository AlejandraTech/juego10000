package com.alejandrapazrivas.juego10000

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase principal de la aplicación que inicializa Hilt para la inyección de dependencias.
 * 
 * Esta clase sirve como punto de entrada para la configuración de la aplicación y
 * está anotada con @HiltAndroidApp para habilitar la generación de componentes de Hilt.
 */
@HiltAndroidApp
class Juego10000Application : Application()