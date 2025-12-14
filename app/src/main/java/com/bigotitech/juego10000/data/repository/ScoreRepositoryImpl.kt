package com.bigotitech.juego10000.data.repository

import com.bigotitech.juego10000.data.local.dao.ScoreDao
import com.bigotitech.juego10000.domain.model.Score
import com.bigotitech.juego10000.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementación del repositorio de puntuaciones.
 * Gestiona la consulta y eliminación de puntuaciones.
 */
class ScoreRepositoryImpl @Inject constructor(
    private val scoreDao: ScoreDao
) : BaseRepository(), ScoreRepository {

    /**
     * Obtiene todas las puntuaciones de una partida.
     * 
     * @param gameId ID de la partida
     * @return Flujo que emite la lista de puntuaciones de la partida
     */
    override fun getScoresByGameId(gameId: Long): Flow<List<Score>> {
        return scoreDao.getScoresByGameId(gameId).map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Obtiene todas las puntuaciones de un jugador.
     * 
     * @param playerId ID del jugador
     * @return Flujo que emite la lista de puntuaciones del jugador
     */
    override fun getScoresByPlayerId(playerId: Long): Flow<List<Score>> {
        return scoreDao.getScoresByPlayerId(playerId)
            .map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Obtiene las puntuaciones de un jugador en una partida específica.
     * 
     * @param gameId ID de la partida
     * @param playerId ID del jugador
     * @return Flujo que emite la lista de puntuaciones del jugador en la partida
     */
    override fun getPlayerGameScores(gameId: Long, playerId: Long): Flow<List<Score>> {
        return scoreDao.getPlayerGameScores(gameId, playerId)
            .map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Obtiene la puntuación total de un jugador en una partida.
     * 
     * @param gameId ID de la partida
     * @param playerId ID del jugador
     * @return Puntuación total del jugador en la partida
     */
    override suspend fun getPlayerTotalScore(gameId: Long, playerId: Long): Int {
        return scoreDao.getPlayerTotalScore(gameId, playerId) ?: 0
    }

    /**
     * Obtiene la puntuación más alta de un jugador.
     * 
     * @param playerId ID del jugador
     * @return Puntuación más alta del jugador
     */
    override suspend fun getPlayerHighestScore(playerId: Long): Int {
        return scoreDao.getPlayerHighestScore(playerId) ?: 0
    }

    /**
     * Obtiene la puntuación media de un jugador.
     * 
     * @param playerId ID del jugador
     * @return Puntuación media del jugador
     */
    override suspend fun getPlayerAverageScore(playerId: Long): Float {
        return scoreDao.getPlayerAverageScore(playerId) ?: 0f
    }

    /**
     * Obtiene el mejor turno de un jugador.
     * 
     * @param playerId ID del jugador
     * @return Objeto Score con la información del mejor turno, o null si no hay turnos
     */
    override suspend fun getPlayerBestTurn(playerId: Long): Score? {
        val scoreEntity = scoreDao.getPlayerBestTurn(playerId) ?: return null
        return scoreEntity.toDomainModel()
    }

    /**
     * Elimina todas las puntuaciones de una partida.
     * 
     * @param gameId ID de la partida
     */
    override suspend fun deleteGameScores(gameId: Long) {
        scoreDao.deleteGameScores(gameId)
    }
}
