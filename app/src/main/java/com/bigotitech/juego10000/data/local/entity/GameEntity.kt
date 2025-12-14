package com.bigotitech.juego10000.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entidad que representa un juego en la base de datos.
 */
@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val gameId: Long = 0,
    val playerIds: List<Long>,
    val winnerPlayerId: Long? = null,
    val targetScore: Int = 10000,
    val isCompleted: Boolean = false,
    val currentPlayerIndex: Int = 0,
    val currentRound: Int = 1,
    val startedAt: Date = Date(),
    val completedAt: Date? = null,

    val gameMode: String = "MULTIPLAYER",  // MULTIPLAYER, SOLO, PRACTICE

    // Estado serializado del juego para restaurar partidas en curso
    val gameState: String? = null
)
