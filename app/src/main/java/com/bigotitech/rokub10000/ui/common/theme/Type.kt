package com.bigotitech.rokub10000.ui.common.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Definición de estilos tipográficos para la aplicación Juego10000
 * Organizado para facilitar su mantenimiento y reutilización
 */

private object TextStyles {
    // Estilos para títulos grandes (Display)
    val DisplayBase = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold
    )
    
    // Estilos para títulos (Headline)
    val HeadlineBase = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold
    )
    
    // Estilos para subtítulos (Title)
    val TitleBase = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium
    )
    
    // Estilos para texto normal (Body)
    val BodyBase = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal
    )
    
    // Estilos para etiquetas (Label)
    val LabelBase = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium
    )
}

// Tipografía
val Typography = Typography(
    // Display styles
    displayLarge = TextStyles.DisplayBase.copy(fontSize = 32.sp),
    displayMedium = TextStyles.DisplayBase.copy(fontSize = 28.sp),
    displaySmall = TextStyles.DisplayBase.copy(fontSize = 24.sp),
    
    // Headline styles
    headlineLarge = TextStyles.HeadlineBase.copy(fontSize = 22.sp),
    headlineMedium = TextStyles.HeadlineBase.copy(fontSize = 20.sp),
    headlineSmall = TextStyles.HeadlineBase.copy(fontSize = 18.sp),
    
    // Title styles
    titleLarge = TextStyles.TitleBase.copy(fontSize = 18.sp),
    titleMedium = TextStyles.TitleBase.copy(fontSize = 16.sp),
    titleSmall = TextStyles.TitleBase.copy(fontSize = 14.sp),
    
    // Body styles
    bodyLarge = TextStyles.BodyBase.copy(fontSize = 16.sp),
    bodyMedium = TextStyles.BodyBase.copy(fontSize = 14.sp),
    bodySmall = TextStyles.BodyBase.copy(fontSize = 12.sp),
    
    // Label styles
    labelLarge = TextStyles.LabelBase.copy(fontSize = 14.sp),
    labelMedium = TextStyles.LabelBase.copy(fontSize = 12.sp),
    labelSmall = TextStyles.LabelBase.copy(fontSize = 10.sp)
)
