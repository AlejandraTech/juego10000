package com.alejandrapazrivas.juego10000.ui.common.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Definición de formas (shapes) para la aplicación Juego10000
 * Organizado para facilitar su mantenimiento y reutilización
 */

// Formas estándar
val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

// Formas específicas para componentes del juego
object GameShapes {
    val Dice = RoundedCornerShape(8.dp)
    val Card = RoundedCornerShape(12.dp)
    val Button = RoundedCornerShape(24.dp)
}

val CardShape = GameShapes.Card
val ButtonShape = GameShapes.Button
