package com.alejandrapazrivas.juego10000.ui.common.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.alejandrapazrivas.juego10000.ui.settings.SettingsViewModel

/**
 * Definición de temas para la aplicación Juego10000
 * Incluye esquemas de colores para temas claro y oscuro
 */

// Esquema de colores para el tema claro
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = LightColors.OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = LightColors.OnPrimary,
    secondary = Secondary,
    onSecondary = LightColors.OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = LightColors.OnSecondary,
    tertiary = Secondary,
    onTertiary = LightColors.OnSecondary,
    background = LightColors.Background,
    onBackground = LightColors.OnBackground,
    surface = LightColors.Surface,
    onSurface = LightColors.OnSurface,
    error = LightColors.Error,
    onError = LightColors.OnError
)

// Esquema de colores para el tema oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = DarkColors.OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = DarkColors.OnPrimary,
    secondary = Secondary,
    onSecondary = DarkColors.OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = DarkColors.OnSecondary,
    tertiary = Secondary,
    onTertiary = DarkColors.OnSecondary,
    background = DarkColors.Background,
    onBackground = DarkColors.OnBackground,
    surface = DarkColors.Surface,
    onSurface = DarkColors.OnSurface,
    error = DarkColors.Error,
    onError = DarkColors.OnError
)

/**
 * Tema principal de la aplicación Juego10000
 * 
 * @param darkTheme Si se debe usar el tema oscuro por defecto (basado en el sistema)
 * @param dynamicColor Si se deben usar colores dinámicos (Android 12+)
 * @param settingsViewModel ViewModel para acceder a las preferencias del usuario
 * @param content Contenido a mostrar con este tema
 */
@Composable
fun Juego10000Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    // Obtener preferencia de tema oscuro del usuario
    val userDarkMode by settingsViewModel.darkMode.collectAsState(initial = false)
    val useDarkTheme = userDarkMode ?: darkTheme

    // Determinar el esquema de colores a utilizar
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Configurar la barra de estado
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !useDarkTheme
        }
    }

    // Calcular información de ventana y dimensiones responsive
    val windowInfo = rememberWindowInfo()
    val dimensions = getDimensionsForWindowType(windowInfo.windowType)

    // Aplicar el tema con dimensiones responsive
    CompositionLocalProvider(
        LocalWindowInfo provides windowInfo,
        LocalDimensions provides dimensions
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
