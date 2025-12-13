package com.alejandrapazrivas.juego10000.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.alejandrapazrivas.juego10000.data.preferences.UserPreferencesManager
import java.util.Locale

/**
 * Helper para gestionar el cambio de idioma de la aplicación
 */
object LocaleHelper {

    /**
     * Aplica el idioma guardado en las preferencias
     * @param languageCode Código del idioma ("es", "en", "system")
     */
    fun setLocale(languageCode: String) {
        val localeList = when (languageCode) {
            UserPreferencesManager.LANGUAGE_SPANISH -> LocaleListCompat.forLanguageTags("es")
            UserPreferencesManager.LANGUAGE_ENGLISH -> LocaleListCompat.forLanguageTags("en")
            else -> LocaleListCompat.getEmptyLocaleList() // System default
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    /**
     * Obtiene el código de idioma actual
     */
    fun getCurrentLocale(context: Context): String {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        return if (currentLocales.isEmpty) {
            UserPreferencesManager.LANGUAGE_SYSTEM
        } else {
            currentLocales[0]?.language ?: UserPreferencesManager.LANGUAGE_SYSTEM
        }
    }
}
