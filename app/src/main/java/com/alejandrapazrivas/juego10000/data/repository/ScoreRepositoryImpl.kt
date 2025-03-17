package com.alejandrapazrivas.juego10000.data.repository

import com.alejandrapazrivas.juego10000.data.local.dao.ScoreDao
import com.alejandrapazrivas.juego10000.data.local.entity.ScoreEntity
import com.alejandrapazrivas.juego10000.domain.model.Score
import com.alejandrapazrivas.juego10000.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScoreRepositoryImpl @Inject constructor(
    private val scoreDao: ScoreDao
) : ScoreRepository {

    override fun getScoresByGameId(gameId: Long): Flow<List<Score>> {
        return scoreDao.getScoresByGameId(gameId).map { list -> list.map { mapEntityToScore(it) } }
    }

    override fun getScoresByPlayerId(playerId: Long): Flow<List<Score>> {
        return scoreDao.getScoresByPlayerId(playerId)
            .map { list -> list.map { mapEntityToScore(it) } }
    }

    override fun getPlayerGameScores(gameId: Long, playerId: Long): Flow<List<Score>> {
        return scoreDao.getPlayerGameScores(gameId, playerId)
            .map { list -> list.map { mapEntityToScore(it) } }
    }

    override suspend fun getPlayerTotalScore(gameId: Long, playerId: Long): Int {
        return scoreDao.getPlayerTotalScore(gameId, playerId) ?: 0
    }

    override suspend fun getPlayerHighestScore(playerId: Long): Int {
        return scoreDao.getPlayerHighestScore(playerId) ?: 0
    }

    override suspend fun getPlayerAverageScore(playerId: Long): Float {
        return scoreDao.getPlayerAverageScore(playerId) ?: 0f
    }

    override suspend fun getPlayerBestTurn(playerId: Long): Score? {
        val scoreEntity = scoreDao.getPlayerBestTurn(playerId) ?: return null
        return mapEntityToScore(scoreEntity)
    }

    override suspend fun deleteGameScores(gameId: Long) {
        scoreDao.deleteGameScores(gameId)
    }

    private fun mapEntityToScore(entity: ScoreEntity): Score {
        return Score(
            id = entity.scoreId,
            gameId = entity.gameId,
            playerId = entity.playerId,
            round = entity.round,
            turnScore = entity.turnScore,
            totalScore = entity.totalScore,
            diceRolls = entity.diceRolls,
            timestamp = entity.timestamp
        )
    }
}