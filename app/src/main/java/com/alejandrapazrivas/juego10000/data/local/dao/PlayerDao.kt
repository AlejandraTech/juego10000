package com.alejandrapazrivas.juego10000.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alejandrapazrivas.juego10000.data.local.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceder y modificar datos de jugadores en la base de datos.
 */
@Dao
interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("SELECT * FROM players WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActivePlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players ORDER BY name ASC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE playerId = :playerId")
    suspend fun getPlayerById(playerId: Long): PlayerEntity?

    @Query("SELECT * FROM players WHERE playerId = :playerId")
    fun getPlayerByIdFlow(playerId: Long): Flow<PlayerEntity?>

    @Query("UPDATE players SET gamesPlayed = gamesPlayed + 1 WHERE playerId IN (:playerIds)")
    suspend fun incrementGamesPlayed(playerIds: List<Long>)

    @Query("UPDATE players SET gamesWon = gamesWon + 1 WHERE playerId = :playerId")
    suspend fun incrementGamesWon(playerId: Long)

    @Query("UPDATE players SET highestScore = :score WHERE playerId = :playerId AND highestScore < :score")
    suspend fun updateHighestScore(playerId: Long, score: Int)

    @Query("UPDATE players SET totalScore = totalScore + :score WHERE playerId = :playerId")
    suspend fun updateTotalScore(playerId: Long, score: Int)
}
