package com.bigotitech.juego10000.ui.stats.model

import com.bigotitech.juego10000.domain.model.Player
import com.bigotitech.juego10000.domain.model.Score

/**
 * Modelo de datos para las estadísticas de un jugador
 */
data class PlayerStats(
    val player: Player,
    val bestScore: Int,
    val averageScore: Float
)

/**
 * Modelo de datos para una puntuación con información del jugador
 */
data class ScoreWithPlayer(
    val score: Score,
    val player: Player
)
