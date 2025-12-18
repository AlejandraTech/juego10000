package com.bigotitech.rokub10000.presentation.feature.stats.state

import com.bigotitech.rokub10000.domain.model.Game
import com.bigotitech.rokub10000.domain.model.Player
import com.bigotitech.rokub10000.domain.model.Score

/**
 * Modelo de datos para las estadísticas de un jugador.
 *
 * @property player El jugador
 * @property bestScore Mejor puntuación obtenida
 * @property averageScore Puntuación media
 */
data class PlayerStats(
    val player: Player,
    val bestScore: Int,
    val averageScore: Float
)

/**
 * Modelo de datos para una puntuación con información del jugador.
 *
 * @property score La puntuación
 * @property player El jugador que obtuvo la puntuación
 */
data class ScoreWithPlayer(
    val score: Score,
    val player: Player
)

/**
 * Modelo de datos para una partida con información del ganador.
 *
 * @property game La partida
 * @property winner El jugador ganador, o null si no hay ganador
 */
data class GameWithWinner(
    val game: Game,
    val winner: Player?
)
