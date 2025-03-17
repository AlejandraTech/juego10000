package com.alejandrapazrivas.juego10000.data.repository

import com.alejandrapazrivas.juego10000.data.local.dao.GameDao
import com.alejandrapazrivas.juego10000.data.local.dao.PlayerDao
import com.alejandrapazrivas.juego10000.data.local.dao.ScoreDao
import com.alejandrapazrivas.juego10000.data.local.entity.GameEntity
import com.alejandrapazrivas.juego10000.data.local.entity.ScoreEntity
import com.alejandrapazrivas.juego10000.domain.model.Game
import com.alejandrapazrivas.juego10000.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    private val playerDao: PlayerDao,
    private val scoreDao: ScoreDao
) : GameRepository {

    override suspend fun createGame(
        playerIds: List<Long>,
        targetScore: Int,
        gameMode: String
    ): Long {
        val gameEntity = GameEntity(
            playerIds = playerIds,
            targetScore = targetScore,
            gameMode = gameMode
        )
        val gameId = gameDao.insertGame(gameEntity)

        // Incrementar el contador de partidas jugadas para cada jugador
        playerDao.incrementGamesPlayed(playerIds)

        return gameId
    }

    override suspend fun getGameById(gameId: Long): Game? {
        val gameEntity = gameDao.getGameById(gameId) ?: return null
        return mapEntityToGame(gameEntity)
    }

    override fun getGameByIdFlow(gameId: Long): Flow<Game?> {
        return gameDao.getGameByIdFlow(gameId).map { it?.let { mapEntityToGame(it) } }
    }

    override fun getActiveGames(): Flow<List<Game>> {
        return gameDao.getActiveGames().map { list -> list.map { mapEntityToGame(it) } }
    }

    override fun getRecentCompletedGames(): Flow<List<Game>> {
        return gameDao.getRecentCompletedGames().map { list -> list.map { mapEntityToGame(it) } }
    }

    override fun getPlayerGames(playerId: Long): Flow<List<Game>> {
        return gameDao.getPlayerGames(playerId).map { list -> list.map { mapEntityToGame(it) } }
    }

    override suspend fun updateGameState(
        gameId: Long,
        currentPlayerIndex: Int,
        currentRound: Int,
        gameState: String?
    ) {
        gameDao.updateGameState(gameId, currentPlayerIndex, currentRound, gameState)
    }

    override suspend fun completeGame(gameId: Long, winnerPlayerId: Long) {
        gameDao.completeGame(gameId, winnerPlayerId)
        playerDao.incrementGamesWon(winnerPlayerId)
    }

    override suspend fun saveScore(
        gameId: Long,
        playerId: Long,
        round: Int,
        turnScore: Int,
        totalScore: Int,
        diceRolls: Int
    ): Long {
        val scoreEntity = ScoreEntity(
            gameId = gameId,
            playerId = playerId,
            round = round,
            turnScore = turnScore,
            totalScore = totalScore,
            diceRolls = diceRolls
        )

        // Actualizar la puntuación más alta del jugador si corresponde
        playerDao.updateHighestScore(playerId, totalScore)
        playerDao.updateTotalScore(playerId, turnScore)

        return scoreDao.insertScore(scoreEntity)
    }

    override suspend fun deleteGame(gameId: Long) {
        gameDao.deleteGame(gameId)
    }

    private fun mapEntityToGame(entity: GameEntity): Game {
        return Game(
            id = entity.gameId,
            playerIds = entity.playerIds,
            winnerPlayerId = entity.winnerPlayerId,
            targetScore = entity.targetScore,
            isCompleted = entity.isCompleted,
            currentPlayerIndex = entity.currentPlayerIndex,
            currentRound = entity.currentRound,
            startedAt = entity.startedAt,
            completedAt = entity.completedAt,
            gameMode = entity.gameMode,
            gameState = entity.gameState
        )
    }
}