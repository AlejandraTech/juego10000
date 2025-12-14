package com.bigotitech.rokub10000.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bigotitech.rokub10000.data.local.entity.ScoreEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceder y modificar datos de puntuaciones en la base de datos.
 */
@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: ScoreEntity): Long

    @Query("SELECT * FROM scores WHERE gameId = :gameId ORDER BY round ASC, timestamp ASC")
    fun getScoresByGameId(gameId: Long): Flow<List<ScoreEntity>>

    @Query("SELECT * FROM scores WHERE playerId = :playerId ORDER BY timestamp DESC")
    fun getScoresByPlayerId(playerId: Long): Flow<List<ScoreEntity>>

    @Query("SELECT * FROM scores WHERE gameId = :gameId AND playerId = :playerId ORDER BY round ASC")
    fun getPlayerGameScores(gameId: Long, playerId: Long): Flow<List<ScoreEntity>>

    @Query("SELECT SUM(turnScore) FROM scores WHERE gameId = :gameId AND playerId = :playerId")
    suspend fun getPlayerTotalScore(gameId: Long, playerId: Long): Int?

    @Query("SELECT MAX(totalScore) FROM scores WHERE playerId = :playerId")
    suspend fun getPlayerHighestScore(playerId: Long): Int?

    @Query("SELECT AVG(turnScore) FROM scores WHERE playerId = :playerId")
    suspend fun getPlayerAverageScore(playerId: Long): Float?

    @Transaction
    @Query("SELECT s.* FROM scores s INNER JOIN games g ON s.gameId = g.gameId WHERE g.isCompleted = 1 AND s.playerId = :playerId ORDER BY s.turnScore DESC LIMIT 1")
    suspend fun getPlayerBestTurn(playerId: Long): ScoreEntity?

    @Query("DELETE FROM scores WHERE gameId = :gameId")
    suspend fun deleteGameScores(gameId: Long)
}
