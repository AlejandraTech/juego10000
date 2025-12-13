package com.alejandrapazrivas.juego10000.ui.common.theme

import androidx.compose.ui.graphics.Color

/**
 * Definición de colores para la aplicación Juego10000
 * Organizado por categorías para facilitar su mantenimiento
 */

// Colores primarios
val Primary = Color(0xFF6200EE)
val PrimaryVariant = Color(0xFF3700B3)
val Secondary = Color(0xFF03DAC6)
val SecondaryVariant = Color(0xFF018786)

// Colores para el tema claro
object LightColors {
    val Background = Color(0xFFF5F5F5)
    val Surface = Color(0xFFFFFFFF)
    val Error = Color(0xFFB00020)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSecondary = Color(0xFF000000)
    val OnBackground = Color(0xFF000000)
    val OnSurface = Color(0xFF000000)
    val OnError = Color(0xFFFFFFFF)
}

// Colores para el tema oscuro
object DarkColors {
    val Background = Color(0xFF121212)
    val Surface = Color(0xFF1E1E1E)
    val Error = Color(0xFFCF6679)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSecondary = Color(0xFF000000)
    val OnBackground = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFFFFFFFF)
    val OnError = Color(0xFF000000)
}

// Colores específicos del juego
object GameColors {
    val ScorePositive = Color(0xFF4CAF50)

    // Colores dorados para victorias y acentos
    val Gold = Color(0xFFFFD700)
    val GoldDark = Color(0xFFDAA520)
    val GoldLight = Color(0xFFFFF8DC)
}

val ScorePositive = GameColors.ScorePositive

// Colores dorados (acceso directo)
val GoldColor = GameColors.Gold
val GoldDark = GameColors.GoldDark
val GoldLight = GameColors.GoldLight
