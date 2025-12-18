package com.bigotitech.rokub10000.domain.repository

import com.bigotitech.rokub10000.domain.model.Score
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar las operaciones relacionadas con puntuaciones.
 */
interface ScoreRepository {
    /**
     * Obtiene todas las puntuaciones de una partida.
     *
     * @param gameId ID de la partida
     * @return Flujo que emite la lista de puntuaciones de la partida
     */
    fun getScoresByGameId(gameId: Long): Flow<List<Score>>

    /**
     * Obtiene todas las puntuaciones de un jugador.
     *
     * @param playerId ID del jugador
     * @return Flujo que emite la lista de puntuaciones del jugador
     */
    fun getScoresByPlayerId(playerId: Long): Flow<List<Score>>

    /**
     * Obtiene las puntuaciones de un jugador en una partida específica.
     *
     * @param gameId ID de la partida
     * @param playerId ID del jugador
     * @return Flujo que emite la lista de puntuaciones del jugador en la partida
     */
    fun getPlayerGameScores(gameId: Long, playerId: Long): Flow<List<Score>>

    /**
     * Obtiene la puntuación total de un jugador en una partida.
     *
     * @param gameId ID de la partida
     * @param playerId ID del jugador
     * @return Puntuación total del jugador en la partida
     */
    suspend fun getPlayerTotalScore(gameId: Long, playerId: Long): Int

    /**
     * Obtiene la puntuación más alta de un jugador.
     *
     * @param playerId ID del jugador
     * @return Puntuación más alta del jugador
     */
    suspend fun getPlayerHighestScore(playerId: Long): Int

    /**
     * Obtiene la puntuación media de un jugador.
     *
     * @param playerId ID del jugador
     * @return Puntuación media del jugador
     */
    suspend fun getPlayerAverageScore(playerId: Long): Float

    /**
     * Obtiene el mejor turno de un jugador.
     *
     * @param playerId ID del jugador
     * @return Objeto Score con la información del mejor turno, o null si no hay turnos
     */
    suspend fun getPlayerBestTurn(playerId: Long): Score?

    /**
     * Elimina todas las puntuaciones de una partida.
     *
     * @param gameId ID de la partida
     */
    suspend fun deleteGameScores(gameId: Long)
}
