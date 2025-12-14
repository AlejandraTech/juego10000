package com.bigotitech.rokub10000.domain.model

import java.util.Date

/**
 * Modelo de datos que representa una partida del juego 10000.
 *
 * @property id Identificador único de la partida
 * @property playerIds Lista de IDs de los jugadores participantes
 * @property winnerPlayerId ID del jugador ganador, o null si la partida no ha terminado
 * @property targetScore Puntuación objetivo para ganar la partida (por defecto 10000)
 * @property isCompleted Indica si la partida ha finalizado
 * @property currentPlayerIndex Índice del jugador actual en la lista de jugadores
 * @property currentRound Número de ronda actual
 * @property startedAt Fecha y hora de inicio de la partida
 * @property completedAt Fecha y hora de finalización de la partida, o null si no ha terminado
 * @property gameMode Modo de juego: "MULTIPLAYER" o "SOLO"
 * @property gameState Estado serializado de la partida para guardar/cargar
 */
data class Game(
    val id: Long = 0,
    val playerIds: List<Long>,
    val winnerPlayerId: Long? = null,
    val targetScore: Int = 10000,
    val isCompleted: Boolean = false,
    val currentPlayerIndex: Int = 0,
    val currentRound: Int = 1,
    val startedAt: Date = Date(),
    val completedAt: Date? = null,
    val gameMode: String = "MULTIPLAYER",
    val gameState: String? = null
) {
    /**
     * Número total de jugadores en la partida.
     * 
     * @return Cantidad de jugadores participantes
     */
    val playerCount: Int
        get() = playerIds.size
}