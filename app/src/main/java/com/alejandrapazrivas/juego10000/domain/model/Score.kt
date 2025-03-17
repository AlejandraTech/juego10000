package com.alejandrapazrivas.juego10000.domain.model

import java.util.Date

/**
 * Modelo de datos que representa una puntuación en el juego.
 *
 * @property id Identificador único de la puntuación
 * @property gameId Identificador de la partida a la que pertenece esta puntuación
 * @property playerId Identificador del jugador que obtuvo esta puntuación
 * @property round Número de ronda en la que se registró esta puntuación
 * @property turnScore Puntuación obtenida en el turno actual
 * @property totalScore Puntuación total acumulada del jugador
 * @property diceRolls Número de lanzamientos de dados realizados en el turno
 * @property timestamp Fecha y hora en que se registró la puntuación
 */
data class Score(
    val id: Long = 0,
    val gameId: Long,
    val playerId: Long,
    val round: Int,
    val turnScore: Int,
    val totalScore: Int,
    val diceRolls: Int = 0,
    val timestamp: Date = Date()
)