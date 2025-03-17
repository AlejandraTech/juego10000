package com.alejandrapazrivas.juego10000.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alejandrapazrivas.juego10000.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * DAO para acceder y modificar datos de juegos en la base de datos.
 */
@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long

    @Query("SELECT * FROM games WHERE gameId = :gameId")
    suspend fun getGameById(gameId: Long): GameEntity?

    @Query("SELECT * FROM games WHERE gameId = :gameId")
    fun getGameByIdFlow(gameId: Long): Flow<GameEntity?>

    @Query("SELECT * FROM games WHERE isCompleted = 0")
    fun getActiveGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE isCompleted = 1 ORDER BY completedAt DESC LIMIT 50")
    fun getRecentCompletedGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE isCompleted = 1 AND :playerId IN (SELECT playerId FROM scores WHERE gameId = games.gameId) ORDER BY completedAt DESC")
    fun getPlayerGames(playerId: Long): Flow<List<GameEntity>>

    @Query("UPDATE games SET isCompleted = 1, winnerPlayerId = :winnerId, completedAt = :completedAt WHERE gameId = :gameId")
    suspend fun completeGame(gameId: Long, winnerId: Long, completedAt: Date = Date())

    @Query("UPDATE games SET currentPlayerIndex = :playerIndex, currentRound = :round, gameState = :gameState WHERE gameId = :gameId")
    suspend fun updateGameState(gameId: Long, playerIndex: Int, round: Int, gameState: String?)

    @Query("DELETE FROM games WHERE gameId = :gameId")
    suspend fun deleteGame(gameId: Long)
}
