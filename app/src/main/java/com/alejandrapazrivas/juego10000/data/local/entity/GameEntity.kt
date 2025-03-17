package com.alejandrapazrivas.juego10000.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.alejandrapazrivas.juego10000.util.DateConverter
import com.alejandrapazrivas.juego10000.util.ListConverter
import java.util.Date

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val gameId: Long = 0,

    @TypeConverters(ListConverter::class)
    val playerIds: List<Long>,

    val winnerPlayerId: Long? = null,
    val targetScore: Int = 10000,
    val isCompleted: Boolean = false,
    val currentPlayerIndex: Int = 0,
    val currentRound: Int = 1,

    @TypeConverters(DateConverter::class)
    val startedAt: Date = Date(),

    @TypeConverters(DateConverter::class)
    val completedAt: Date? = null,

    val gameMode: String = "MULTIPLAYER",  // MULTIPLAYER, SOLO, PRACTICE

    // Estado serializado del juego para restaurar partidas en curso
    val gameState: String? = null
)