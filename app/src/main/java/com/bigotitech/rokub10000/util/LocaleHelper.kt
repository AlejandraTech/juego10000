package com.bigotitech.rokub10000.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager

/**
 * Helper para gestionar el cambio de idioma de la aplicación
 */
object LocaleHelper {

    /**
     * Aplica el idioma guardado en las preferencias.
     * Solo cambia el locale si es diferente al actual para evitar
     * recreaciones de Activity innecesarias (que causan parpadeo negro).
     * @param languageCode Código del idioma ("es", "en", "ca", "fr", "system")
     */
    fun setLocale(languageCode: String) {
        val newLocaleList = when (languageCode) {
            UserPreferencesManager.LANGUAGE_SPANISH -> LocaleListCompat.forLanguageTags("es")
            UserPreferencesManager.LANGUAGE_ENGLISH -> LocaleListCompat.forLanguageTags("en")
            UserPreferencesManager.LANGUAGE_CATALAN -> LocaleListCompat.forLanguageTags("ca")
            UserPreferencesManager.LANGUAGE_FRENCH -> LocaleListCompat.forLanguageTags("fr")
            else -> LocaleListCompat.getEmptyLocaleList() // System default
        }

        // Verificar si el locale actual es diferente del nuevo
        val currentLocaleList = AppCompatDelegate.getApplicationLocales()

        // Comparar los locales: evitar cambio si ya están configurados igual
        val currentLanguage = if (currentLocaleList.isEmpty) null else currentLocaleList[0]?.language
        val newLanguage = if (newLocaleList.isEmpty) null else newLocaleList[0]?.language

        // Solo aplicar cambio si el idioma es realmente diferente
        if (currentLanguage != newLanguage) {
            AppCompatDelegate.setApplicationLocales(newLocaleList)
        }
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
