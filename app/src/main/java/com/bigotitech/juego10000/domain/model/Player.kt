package com.bigotitech.juego10000.domain.model

/**
 * Modelo de datos que representa un jugador en el juego.
 *
 * @property id Identificador único del jugador
 * @property name Nombre del jugador
 * @property avatarResourceId ID del recurso de avatar del jugador
 * @property gamesPlayed Número total de partidas jugadas
 * @property gamesWon Número total de partidas ganadas
 * @property highestScore Puntuación más alta obtenida en una partida
 * @property totalScore Suma total de puntos obtenidos en todas las partidas
 * @property isActive Indica si el jugador está activo en el sistema
 * @property createdAt Timestamp de cuando se creó el perfil del jugador
 */
data class Player(
    val id: Long = 0,
    val name: String,
    val avatarResourceId: Int = 0,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val highestScore: Int = 0,
    val totalScore: Long = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Porcentaje de victorias del jugador.
     * 
     * @return Proporción de partidas ganadas respecto a las jugadas, o 0 si no ha jugado ninguna
     */
    val winRate: Float
        get() = if (gamesPlayed > 0) gamesWon.toFloat() / gamesPlayed.toFloat() else 0f
}